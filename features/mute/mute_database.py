"""
Goosenet
Goosenet is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
import os
import aiosqlite
from discord.ext import commands

# DATABASE
class MuteDatabase:
	_instance = None
	_db = None

	def __new__(cls):
		if cls._instance is None:
			cls._instance = super().__new__(cls)
		return cls._instance

	async def initialize(self):
		if self._db is None:
			self._db = await aiosqlite.connect(os.path.expanduser('features/mute/mute_database.db'))

			await self._db.execute('''CREATE TABLE IF NOT EXISTS image_hashes (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				message_id INTEGER,
				user_id INTEGER,
				channel_id INTEGER,
				image_url TEXT,
				phash TEXT,
				created_at DATETIME DEFAULT CURRENT_TIMESTAMP
			)''')

			await self._db.execute('''CREATE TABLE IF NOT EXISTS banned_images (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				phash TEXT NOT NULL UNIQUE
			)''')

			await self._db.execute('''CREATE TABLE IF NOT EXISTS monitored_roles (
				guild_id INTEGER,
				role_id INTEGER,
				PRIMARY KEY (guild_id, role_id)
			)''')

			await self._db.execute('''CREATE TABLE IF NOT EXISTS guild_settings (
				guild_id INTEGER PRIMARY KEY,
				feedback_channel_id INTEGER
			)''')

			await self._db.execute('CREATE INDEX IF NOT EXISTS idx_phash ON image_hashes(phash)')
			await self._db.execute('CREATE INDEX IF NOT EXISTS idx_banned_phash ON banned_images(phash)')
			await self._db.commit()

	async def close(self):
		if self._db:
			await self._db.close()
			self._db = None

	@property
	def db(self):
		return self._db

# COGS
class MuteStorage(commands.Cog):
	def __init__(self, bot):
		self.bot = bot
		self._db_manager = MuteDatabase()

	async def cog_load(self):
		await self._db_manager.initialize()

	async def cog_unload(self):
		await self._db_manager.close()

async def setup(bot):
	await bot.add_cog(MuteStorage(bot))