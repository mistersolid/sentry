"""
Goosenet
Goosenet is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
from config import client
from features.mute.mute_logic import MuteFeature
from features.mute.mute_database import MuteStorage
from features.ask.ask_command import AskFeature
from features.ask.ask_listener import AskListener

# LOAD COGS
@client.goosenet.event
async def on_ready():
    assert client.goosenet.user is not None

    print(f'Logged in as {client.goosenet.user} (ID: {client.goosenet.user.id})')

    if not client.goosenet.get_cog("MuteStorage"):
        print("Adding MuteDatabase cog...")
        await client.goosenet.add_cog(MuteStorage(client.goosenet))
        print("MuteDatabase cog added.")
    else:
        print("MuteDatabase cog already loaded.")

    if not client.goosenet.get_cog("MuteFeature"):
        print("Adding MuteFeature cog...")
        await client.goosenet.add_cog(MuteFeature(client.goosenet))
        print("MuteFeature cog added.")
    else:
        print("MuteFeature cog already loaded.")

    if not client.goosenet.get_cog("AskFeature"):
        print("Adding AskFeature cog...")
        await client.goosenet.add_cog(AskFeature(client.goosenet))
        print("AskFeature cog added.")

    if not client.goosenet.get_cog("AskListener"):
        print("Adding AskListener cog...")
        await client.goosenet.add_cog(AskListener(client.goosenet))
        print("AskListener cog added.")