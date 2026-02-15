"""
Goosenet
Goosenet is an anti-spam and anti-scam bot. Skynet but for the goose servers.
"""

# IMPORTS
from __future__ import annotations
from dataclasses import dataclass

@dataclass
class Tools:
    name: str
    mute_statement: str


@dataclass
class Geese:
    permanent: bool
    name: str
    title: str
    tool: Tools
    mute_count: int
    honk_count: int

# 1. Physics
quantum_goose = Geese(
    permanent=True,
    name="Quantum Goose",
    title="Observer of Superpositioned Spam",
    tool=Tools(
        name="Wave Function Honkllapse",
        mute_statement="**HONK!** {user}'s spam exists in a state of MUTED and BANNED. I just collapsed it to both."
    ),
    mute_count=0,
    honk_count=0
)

# 2. Chemistry
chemistry_goose = Geese(
    permanent=True,
    name="Catalytic Goose",
    title="Breaker of Toxic Bonds",
    tool=Tools(
        name="Exothermic Honk Reaction",
        mute_statement="**HONK!** {user}'s message was volatile and unstable. I've neutralized the reaction."
    ),
    mute_count=0,
    honk_count=0
)

# 3. Biology
genome_goose = Geese(
    permanent=True,
    name="Genome Goose",
    title="Sequencer of Suspicious Strands",
    tool=Tools(
        name="CRISPR-Honk9",
        mute_statement="**HONK!** I've identified the spam gene in {user}'s message and spliced it out of existence."
    ),
    mute_count=0,
    honk_count=0
)

# 4. Astronomy
nebula_goose = Geese(
    permanent=True,
    name="Nebula Goose",
    title="Warden of the Cosmic Pond",
    tool=Tools(
        name="Supernova Honk Blast",
        mute_statement="**HONK!** {user}'s spam has been ejected beyond the event horizon. No light escapes, and neither do they."
    ),
    mute_count=0,
    honk_count=0
)

# 5. Geology
tectonic_goose = Geese(
    permanent=True,
    name="Tectonic Goose",
    title="Shifter of Continental Bans",
    tool=Tools(
        name="Seismic Honk Wave",
        mute_statement="**HONK!** A magnitude 9.0 mute has fractured {user}'s ability to type. Tectonic silence achieved."
    ),
    mute_count=0,
    honk_count=0
)

# 6. Neuroscience
synapse_goose = Geese(
    permanent=True,
    name="Synapse Goose",
    title="Conductor of Neural Silence",
    tool=Tools(
        name="Dopamine Honk Blocker",
        mute_statement="**HONK!** I've inhibited the neurotransmitters responsible for {user}'s spam impulse. Synaptic silence."
    ),
    mute_count=0,
    honk_count=0
)

# 7. Mathematics
fractal_goose = Geese(
    permanent=True,
    name="Fractal Goose",
    title="Iterator of Infinite Bans",
    tool=Tools(
        name="Mandelbrot Honk Set",
        mute_statement="**HONK!** {user}'s mute is recursively defined; zoom in and it's still a mute. At every scale. Forever."
    ),
    mute_count=0,
    honk_count=0
)

# 8. Ecology
ecosystem_goose = Geese(
    permanent=True,
    name="Ecosystem Goose",
    title="Balancer of the Digital Biome",
    tool=Tools(
        name="Apex Predator Honk",
        mute_statement="**HONK!** {user} was an invasive species in this server. I've restored ecological balance."
    ),
    mute_count=0,
    honk_count=0
)

# 9. Paleontology
fossil_goose = Geese(
    permanent=True,
    name="Fossil Goose",
    title="Excavator of Ancient Scams",
    tool=Tools(
        name="Carbonized Honk Dating",
        mute_statement="**HONK!** Carbon dating confirms {user}'s scam is as old as the Cretaceous. Fossilized and archived."
    ),
    mute_count=0,
    honk_count=0
)

# 10. Thermodynamics
entropy_goose = Geese(
    permanent=True,
    name="Entropy Goose",
    title="Enforcer of the Second Honk Law",
    tool=Tools(
        name="Heat Death Honk",
        mute_statement="**HONK!** The entropy of {user}'s chat privileges has reached maximum disorder. This process is irreversible."
    ),
    mute_count=0,
    honk_count=0
)

# 11. Microbiology
pathogen_goose = Geese(
    permanent=True,
    name="Pathogen Goose",
    title="Antibody of the Server Immune System",
    tool=Tools(
        name="Phagocytic Honk Response",
        mute_statement="**HONK!** {user}'s spam was flagged as a foreign antigen. Immune response activated. They've been engulfed."
    ),
    mute_count=0,
    honk_count=0
)

# 12. Electromagnetism
tesla_goose = Geese(
    permanent=True,
    name="Tesla Goose",
    title="Conductor of Electromagnetic Bans",
    tool=Tools(
        name="Faraday Honk Cage",
        mute_statement="**HONK!** I've enclosed {user} in a Faraday cage of silence. No signals in, no spam out."
    ),
    mute_count=0,
    honk_count=0
)

# ROSTER
science_flock = [
    quantum_goose,
    chemistry_goose,
    genome_goose,
    nebula_goose,
    tectonic_goose,
    synapse_goose,
    fractal_goose,
    ecosystem_goose,
    fossil_goose,
    entropy_goose,
    pathogen_goose,
    tesla_goose,
]