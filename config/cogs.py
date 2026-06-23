"""
Sentry
Sentry is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
from config import client
from features.mute.mute_logic import MuteFeature
from features.mute.mute_database import MuteStorage
from features.ask.ask_command import AskFeature
from features.ask.ask_listener import AskListener

# LOAD COGS
@client.sentry.event
async def on_ready():
    assert client.sentry.user is not None

    print(f'Logged in as {client.sentry.user} (ID: {client.sentry.user.id})')

    # MUTE STORAGE
    if not client.sentry.get_cog("MuteStorage"):
        print("Adding MuteDatabase cog...")
        await client.sentry.add_cog(MuteStorage(client.sentry))
        print("MuteDatabase cog added.")
    else:
        print("MuteDatabase cog already loaded.")

    # MUTE FEATURE
    if not client.sentry.get_cog("MuteFeature"):
        print("Adding MuteFeature cog...")
        await client.sentry.add_cog(MuteFeature(client.sentry))
        print("MuteFeature cog added.")
    else:
        print("MuteFeature cog already loaded.")

    # ASK FEATURE
    if not client.sentry.get_cog("AskFeature"):
        print("Adding AskFeature cog...")
        await client.sentry.add_cog(AskFeature(client.sentry))
        print("AskFeature cog added.")

    # ASK LISTENER
    if not client.sentry.get_cog("AskListener"):
        print("Adding AskListener cog...")
        await client.sentry.add_cog(AskListener(client.sentry))
        print("AskListener cog added.")