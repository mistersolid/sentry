// PACKAGE
package features.welcomeMessaging

// IMPORT
import core.GuildRole.BACHELORS
import core.GuildRole.BIOLOGY
import core.GuildRole.CHEMISTRY
import core.GuildRole.COMPUTER_SCIENCE
import core.GuildRole.DATA_SCIENCE
import core.GuildRole.ENGINEERING
import core.GuildRole.GRAD_STUDENT
import core.GuildRole.HIGH_SCHOOL
import core.GuildRole.MATHEMATICS
import core.GuildRole.PHYSICS
import core.GuildRole.PLANETARY_SCIENCE
import core.GuildRole.SOCIAL_SCIENCES
import core.Profile

// CONSTANT
const val WEIGHT_DEFAULT = 100

/** Generic prompts are drawn less often than role-specific ones. */
const val WEIGHT_GENERIC = 65

// CLASS
data class PromptEntry(
    val text: String,
    val weight: Int = WEIGHT_DEFAULT,
    val applies: (Profile) -> Boolean = { true },
)

// FUNCTION
private fun group(
    vararg texts: String,
    weight: Int = WEIGHT_DEFAULT,
    applies: (Profile) -> Boolean = { true },
): List<PromptEntry> = texts.map { PromptEntry(it, weight, applies) }

// CATALOG
val promptCatalog: List<PromptEntry> = buildList {

    // ============= DEFAULT =============
    addAll(
        group(
            "📚 What are you currently learning?",
            "🍽️ If you could have lunch with any person in STEM, who would it be?",
            "🗣️ Which topic could you talk about for hours? (STEM or non-STEM)",
            "🤯 What's the last thing you learned that surprised you?",
            "📰 What recent news have you found most striking?",
            "🚀 If you could work (or have worked) on any project, what would it be?",
            "🧠 If you could instantly master one STEM skill, what would it be?",
            "💡 What's a concept that completely changed how you think about something?",
            "🔍 What topic do you want to dive deeper into next?",
            "📈 What's one skill you're trying to improve?  (Currently or in the future)",
            "⚙️ What's the most underrated technology today?",
            "🚀 What technology are you most excited for?",
            "🌍 What real-world problem would you most want to help solve?",
            "💡 What factoid do you think more people should know?",
            "🏛️ Which historical figure do you think is underrated?",
            "🔮 What's one technology you hope to see in your lifetime?",
            weight = WEIGHT_GENERIC,
        )
    )
    add(PromptEntry("🌍 What problem would you love to solve?", weight = 30))
    add(
        PromptEntry(
            "🥚 Whoa!  You found the super rare easter egg prompt!  Tell us your favorite easter egg!",
            weight = 1,
        )
    )

    // ============= SUBJECT =============
    addAll(
        group(
            "🤝 What social issue do you think deserves more research attention?",
            "🎲 What's your favorite cognitive bias?",
        ) { SOCIAL_SCIENCES in it }
    )

    addAll(
        group(
            "🧬 What biological discovery do you think changed the world the most?",
        ) { BIOLOGY in it }
    )

    addAll(
        group(
            "⚗️ What chemical reaction or process do you find most fascinating?",
        ) { CHEMISTRY in it }
    )

    addAll(
        group(
            "🛠️ What's something you're building right now?  Or want to build?",
            "🏗️ What engineering challenge do you think is the hardest to solve right now?",
            "💰 If you had unlimited funding, what would you build?",
            "🔗 Which interdisciplinary field fascinates you most?",
            "🔄 Which failed invention do you think deserves a second chance?",
        ) { ENGINEERING in it }
    )

    addAll(
        group(
            "💻 What area of computer science do you think is most underexplored?",
            "🤖 What's one task you think humans should never fully automate?",
            "🖥️ What bug annoyed you the most?",
        ) { COMPUTER_SCIENCE in it }
    )

    addAll(
        group(
            "📊 What dataset or data problem do you find most interesting to work with?",
            "📉 What everyday problem do you wish more data could solve?",
            "📋 If you could survey everyone, what would you look for?  What would you ask?",
        ) { DATA_SCIENCE in it }
    )

    addAll(
        group(
            "➗ What unsolved math problem fascinates you the most?",
            "🔢 Do you think the Collatz Conjecture will ever be solved?",
            "💭 Which 'obvious' assumption do you question the most?",
        ) { MATHEMATICS in it }
    )

    addAll(
        group(
            "🌌 What physics phenomenon do you find most mind-blowing?",
        ) { PHYSICS in it }
    )

    addAll(
        group(
            "🪐 What planetary body or feature are you most curious about? (Earth included!)",
            "🛰️ Which mission or experiment do you wish you could have witnessed?",
            weight = WEIGHT_GENERIC,
        ) { PLANETARY_SCIENCE in it }
    )
    add(
        PromptEntry(
            "🔭 What part of planetary science are you most interested in?",
            weight = 500,
        ) { PLANETARY_SCIENCE in it }
    )

    // ============= COMPOSITE =============
    addAll(
        group(
            "❓ What scientific mystery do you most want answered?",
            "🧪 What experiment would you run if there were essentially no budget limits?",
            "🌊 Which natural phenomenon never stops amazing you?",
        ) { it.natural }
    )

    addAll(
        group(
            "🔍 What's a mystery that seems like it should already be solved?",
            "🧮 What's a theorem or result that feels almost magical to you?",
        ) { it.natural || MATHEMATICS in it }
    )

    addAll(
        group(
            "⚗️ Which experiment would you never get tired of watching?",
            "🧪 What's the most elegant experiment you've ever heard of?",
            "🧬 Which discovery do you think happened surprisingly late in history?",
        ) { it.experimental }
    )

    addAll(
        group(
            "🌌 What scale of the universe do you think about the most?",
            "🕰️ Which scientific prediction are you most curious to see come true?  (Or fail)",
            "📡 What signal or observation would you most want to detect?",
            "🪐 Which planet or moon do you think deserves more attention?",
        ) { PHYSICS in it || PLANETARY_SCIENCE in it }
    )

    addAll(
        group(
            "🔋 Which breakthrough would change everyday life the fastest?",
            "🦠 What's a tiny thing that has had an enormous impact?",
        ) { BIOLOGY in it || CHEMISTRY in it || PHYSICS in it }
    )

    addAll(
        group(
            "🧊 Which material do you think has untapped potential?",
        ) { CHEMISTRY in it || PHYSICS in it }
    )

    addAll(
        group(
            "📈 What's a graph you think everyone should see once?",
            "📉 What's a statistic you find hard to believe?",
        ) { DATA_SCIENCE in it || SOCIAL_SCIENCES in it }
    )

    // ============= BACKGROUND =============
    addAll(
        group(
            "🔬 Other than your own, what's your favorite area of STEM?",
            "📖 What's a book, paper, or course you'd recommend?",
            "💭 What's a side project you've always wanted to start?",
            "🏆 What project taught you the most?",
            "🌱 What got you interested in your current field?",
            "🌟 What would you like to be known for in your field?",
            "🎯 What are you hoping to achieve in the next 5 or 10 years?",
            "🧰 What skill from your degree do you use the most in everyday life?",
            "✨ What inspired you to get into STEM?",
            "📢 What subject do you wish more people understood?",
            "🚧 What's a common misconception about your field?",
            "🔗 Which interdisciplinary field fascinates you most?",
            "🔬 What research tool or method has made the biggest difference in your work?",
            "🛠️ What are you working on right now? (Project or otherwise)",
            "🚀 What's the coolest project you've worked on?",
            "🌱 What's a small habit that noticeably improved how you learn?",
            "📚 Which paper or book changed how you think?",
        ) { HIGH_SCHOOL !in it }
    )

    addAll(
        group(
            "✨ What's something you understand now that once felt impossible?",
            "🏫 What subject in school has surprised you the most?",
            "🎓 What are you hoping to do after graduating?",
            "📅 What topic are you most excited to study soon?",
            "📅 How many more years of school do you think you'll have?",
            "🎓 Are you looking forward to graduating?",
        ) { it.student }
    )

    addAll(
        group(
            "🚀 What's your dream job or dream research area?",
        ) { it.student && HIGH_SCHOOL !in it }
    )

    addAll(
        group(
            "🕳️ What gap in your field do you think your thesis (or work) could help fill?",
            "💎 What aspect of your research/work are you most proud of?",
            "🧑‍🔬 What are you currently researching or involved with?  (Or planning!)",
        ) { GRAD_STUDENT in it }
    )

    addAll(
        group(
            "🌱 What got you interested in your current field?",
            "🌙 What research question keeps you up at night?",
            "🧠 What's a question you wish someone would ask you?",
            "🔬 Which scientific idea do you think is stranger than science fiction?",
            "🧩 Which problem feels deceptively simple to you?",
            "🛠️ What's a tool you couldn't imagine doing your work without?",
        ) { it.graduate }
    )

    @Suppress("UNUSED_EXPRESSION") BACHELORS
}