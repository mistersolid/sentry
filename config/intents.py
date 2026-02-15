"""
Goosenet
Goosenet is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
import discord
from discord.ext import commands

# INTENT VALUES
intents = discord.Intents.default()
intents.message_content = True
intents.members = True
intents.bans = True
intents.guild_typing = True
intents.guild_messages = True
intents.moderation = True
intents.guild_typing = True
intents.reactions = True