"""
Sentry
Sentry is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
from discord import app_commands
from discord.ext import commands

# FEATURE
class AskFeature(commands.Cog):
	def __init__(self, bot):
		self.bot = bot

	# TOGGLE VALUE
	ask_listener_on: bool = True

	# COMMAND
	@app_commands.command(name='ask-toggle')
	@app_commands.checks.has_permissions(administrator=True)
	async def ask_toggle(self, interaction):
		self.ask_listener_on = not self.ask_listener_on
		# COMMAND FEEDBACK
		await interaction.response.send_message(f'Ask listener is now {self.ask_listener_on}')