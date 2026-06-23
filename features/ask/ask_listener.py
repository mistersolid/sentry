"""
Sentry
Sentry is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
import sys
import joblib
from discord.ext import commands
from features.ask.ask_command import AskFeature
from features.ask.transformers import EdgeCaseFeatureExtractor

import __main__
__main__.EdgeCaseFeatureExtractor = EdgeCaseFeatureExtractor

# SUPERVISORS
SUPERVISORS = {
	395419912845393923,  # Thomas
	228331610444005377,  # Kiftz
	496918293509308419   # Ry
}

# LISTENER
class AskListener(commands.Cog):
	def __init__(self, bot):
		self.bot = bot
		self.message_map = {}
		self.model = joblib.load('features/ask/model.pkl')
		self.feature_extractor = joblib.load('features/ask/vectorizer.pkl')

	def _predict(self, message):
		# VECTORIZE - the feature_extractor handles both TF-IDF and custom features
		message_vec = self.feature_extractor.transform([message])

		prediction = self.model.predict(message_vec)[0]
		probabilities = self.model.predict_proba(message_vec)[0]
		probability = probabilities[1]

		return prediction, probability

	@commands.Cog.listener()
	async def on_message(self, message):
		if message.author.bot:
			return

		if AskFeature.ask_listener_on:
			if 3 < len(message.content) < 90:
				channel = message.channel

				# Run CPU-bound prediction in a thread pool to avoid blocking the event loop
				prediction, probability = await self.bot.loop.run_in_executor(
					None, self._predict, message.content
				)

				# Lecture the person asking to ask
				if probability > 0.95:
					bot_message = await channel.send(
						f'{message.author.mention}. **HONK!** Ask your question directly, there\'s no '
						f'need to ask to ask! :) *I am just a robo-goose and can make mistakes!*'
					)
					# Store the mapping
					self.message_map[bot_message.id] = message.content
					return

	# SUPERVISOR REACTIONS
	@commands.Cog.listener()
	async def on_reaction_add(self, reaction, user):
		if user.bot:
			return

		if user.id not in SUPERVISORS:
			return

		if reaction.message.id not in self.message_map:
			return

		original_message = self.message_map[reaction.message.id]

		if str(reaction.emoji) == '❤️':
			target_user = await self.bot.fetch_user(496918293509308419)
			await target_user.send(f'{original_message} "TRUE"')

		elif str(reaction.emoji) == '👎':
			target_user = await self.bot.fetch_user(496918293509308419)
			await target_user.send(f'{original_message} "FALSE"')


async def setup(bot):
	await bot.add_cog(AskListener(bot))