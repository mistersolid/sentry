"""
Sentry
Sentry is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
import os
from config import client, keys, cogs
os.environ['OMP_NUM_THREADS'] = '1'
os.environ['MKL_NUM_THREADS'] = '1'
os.environ['OPENBLAS_NUM_THREADS'] = '1'
os.environ['VECLIB_MAXIMUM_THREADS'] = '1'
os.environ['NUMEXPR_NUM_THREADS'] = '1'

# MAIN
if __name__ == "__main__":
	client.sentry.run(os.getenv('DISCORD_TOKEN'))