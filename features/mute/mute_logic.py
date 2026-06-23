"""
Sentry
Sentry is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
import discord
from discord.ext import commands
from discord import app_commands, Forbidden
import datetime
import random
import re
from PIL import Image, ImageFile
import imagehash
from io import BytesIO
import urllib.request
import warnings

# HANDLE TRUNCATED IMAGES
ImageFile.LOAD_TRUNCATED_IMAGES = True
warnings.filterwarnings('ignore', message='Truncated File Read', module='PIL')
from features.mute.mute_database import MuteDatabase


# FUNCTIONS
def sync_image_url_to_phash(url: str) -> str:
    req = urllib.request.Request(
        url,
        headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}
    )
    with urllib.request.urlopen(req) as resp:
        data = resp.read()
    image = Image.open(BytesIO(data))
    return str(imagehash.phash(image))


# COG
class MuteFeature(commands.Cog):
    HAMMING_THRESHOLD = 10  # 6 out of 64 bits = ~85% similarity

    def __init__(self, bot):
        self.bot = bot
        self._db_manager = MuteDatabase()

    @property
    def db(self):
        return self._db_manager.db

    async def get_monitored_roles(self, guild_id):
        async with self.db.execute('SELECT role_id FROM monitored_roles WHERE guild_id = ?', (guild_id,)) as cursor:
            return {row[0] for row in await cursor.fetchall()}

    async def get_feedback_channel_id(self, guild_id):
        async with self.db.execute('SELECT feedback_channel_id FROM guild_settings WHERE guild_id = ?',
                                   (guild_id,)) as cursor:
            row = await cursor.fetchone()
            return row[0] if row else None

    async def send_feedback(self, message, text):
        ch_id = await self.get_feedback_channel_id(message.guild.id)
        channel = message.guild.get_channel(ch_id) if ch_id else None
        target = channel or message.channel
        try:
            await target.send(text)
        except Forbidden:
            if target != message.channel:
                await message.channel.send(f'Could not send to {target.mention}. {text}')

    # MUTE AND CLEANUP
    async def _mute_and_cleanup(self, message, reason, feedback_text):
        try:
            await message.author.timeout(discord.utils.utcnow() + datetime.timedelta(weeks=1),
                                         reason=reason)
            async for msg in message.channel.history(limit=10, before=message):
                if msg.author.id == message.author.id:
                    await msg.delete()
                else:
                    break
            await message.delete()

            await self.send_feedback(message, feedback_text)
        except Forbidden:
            await self.send_feedback(message,
                                     f'**Action failed**\nUser: {message.author.mention}\n'
                                     f'Reason: Bot lacks permission or role hierarchy is insufficient.')

    # COMMANDS
    # MUTE IMAGE
    @app_commands.command(name='mute-image', description='Ban an image by URL so it triggers auto-mute')
    @app_commands.checks.has_permissions(ban_members=True)
    @app_commands.guild_only()
    async def ban_image(self, interaction, url: str):
        await interaction.response.defer(ephemeral=True)
        phash = await self.bot.loop.run_in_executor(None, sync_image_url_to_phash, url)

        await self.db.execute('INSERT OR IGNORE INTO banned_images (phash) VALUES (?)', (phash,))
        await self.db.commit()

        await interaction.followup.send(f'Image registered.\npHash: `{phash}`', ephemeral=True)

    # MONITORED ROLES
    @app_commands.command(name='monitored-roles', description='Manage roles that the bot listens to for image matching')
    @app_commands.checks.has_permissions(administrator=True)
    @app_commands.guild_only()
    @app_commands.choices(action=[
        app_commands.Choice(name='add', value='add'),
        app_commands.Choice(name='remove', value='remove'),
        app_commands.Choice(name='list', value='list')
    ])
    async def monitored_roles(self, interaction, action: str, role: discord.Role = None):
        await interaction.response.defer(ephemeral=True)

        if action in ('add', 'remove') and not role:
            await interaction.followup.send(f'You must specify a role to {action}.', ephemeral=True)
            return

        if action == 'add':
            await self.db.execute('INSERT OR IGNORE INTO monitored_roles (guild_id, role_id) VALUES (?, ?)',
                                  (interaction.guild.id, role.id))
            await self.db.commit()
            await interaction.followup.send(f'Added {role.mention} to monitored roles.', ephemeral=True)

        elif action == 'remove':
            await self.db.execute('DELETE FROM monitored_roles WHERE guild_id = ? AND role_id = ?',
                                  (interaction.guild.id, role.id))
            await self.db.commit()
            await interaction.followup.send(f'Removed {role.mention} from monitored roles.', ephemeral=True)

        elif action == 'list':
            roles = await self.get_monitored_roles(interaction.guild.id)
            if not roles:
                await interaction.followup.send(
                    'No roles are currently monitored. The bot is currently ignoring all users (except admins).',
                    ephemeral=True)
            else:
                role_mentions = [f'<@&{r_id}>' for r_id in roles]
                await interaction.followup.send(f'**Monitored Roles:**\n' + '\n'.join(role_mentions), ephemeral=True)

    # FEEDBACK
    @app_commands.command(name='set-feedback-channel',
                          description='Set the channel where enforcement feedback messages are sent')
    @app_commands.checks.has_permissions(administrator=True)
    @app_commands.guild_only()
    async def set_feedback_channel(self, interaction, channel: discord.TextChannel):
        await interaction.response.defer(ephemeral=True)
        await self.db.execute(
            'INSERT INTO guild_settings (guild_id, feedback_channel_id) VALUES (?, ?) '
            'ON CONFLICT(guild_id) DO UPDATE SET feedback_channel_id = excluded.feedback_channel_id',
            (interaction.guild.id, channel.id))
        await self.db.commit()
        await interaction.followup.send(f'Feedback channel set to {channel.mention}.', ephemeral=True)

    @commands.Cog.listener()
    async def on_message(self, message):
        if message.author.bot or not message.guild or message.author.guild_permissions.administrator:
            return

        monitored = await self.get_monitored_roles(message.guild.id)
        if not monitored or not monitored & {r.id for r in message.author.roles}:
            return

        # MUTE & DELETE DISCORD INVITE LINKS
        # FOR USERS <100 MESSAGES
        invite_pattern = re.compile(r'(discord\.(gg|io|me|li)|discordapp\.com/invite|discord\.com/invite)/[a-zA-Z0-9-]+', re.IGNORECASE)
        if invite_pattern.search(message.content):
            # Count how many messages the user has sent in the server
            message_count = 0
            for channel in message.guild.text_channels:
                try:
                    async for msg in channel.history(limit=200):
                        if msg.author.id == message.author.id:
                            message_count += 1
                            if message_count >= 100:
                                break
                except (Forbidden, Exception):
                    continue
                if message_count >= 100:
                    break

            if message_count < 100:
                # Delete messages from the last 2 minutes
                two_minutes_ago = discord.utils.utcnow() - datetime.timedelta(minutes=2)
                try:
                    async for msg in message.channel.history(limit=100, after=two_minutes_ago):
                        if msg.author.id == message.author.id:
                            await msg.delete()
                except (Forbidden, Exception):
                    pass

        # MUTE & DELETE BANNED IMAGES
        for att in message.attachments:
            if not (att.content_type and att.content_type.startswith('image/')):
                continue

            phash = await self.bot.loop.run_in_executor(None, sync_image_url_to_phash, att.url)
            current = imagehash.hex_to_hash(phash)

            async with self.db.execute('SELECT phash FROM banned_images') as cursor:
                banned = await cursor.fetchall()

            if any(current - imagehash.hex_to_hash(row[0]) <= self.HAMMING_THRESHOLD for row in banned):
                await self._mute_and_cleanup(
                    message,
                    reason='Uploaded banned image (≥90% hash match)',
                    feedback_text=f'**HONK!**\n{message.author.mention} has been muted for uploading a banned image.'
                )
                return

            await self.db.execute(
                'INSERT INTO image_hashes (message_id, user_id, channel_id, image_url, phash) VALUES (?, ?, ?, ?, ?)',
                (message.id, message.author.id, message.channel.id, att.url, phash))
            await self.db.commit()


async def setup(bot):
    await bot.add_cog(MuteFeature(bot))