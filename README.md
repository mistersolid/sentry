# Sentry

Last updated: August 27, 2026
Status: In progress
A Kotlin Discord bot built on [Kord](https://github.com/kordlib/kord).

## LLM usage rules

Contributors are expected to implement functionality on their own, only using LLMs in a manner similar to 
StackOverflow, Google, YouTube, etc.
Do not copy and paste large blocks of code from LLMs. You may use LLMs for generating documentation and tests, 
given that you review the output and ensure accuracy.

## Module map

```
sentry/
├── src/   Kotlin-Toolchain entry point
├── bot/           main(), config load, feature registration
├── core/          Pure domain: models, rules. No Kord or Discord API
├── persistence/   Database access
├── framework/     Bot machinery: Feature API, commands, config
├── features/
│   ├── logging/
│   ├── phashMatching/
│   ├── roleManagement/
│   └── welcomeMessaging/
```

When cloning the project, create a new .env and config.json file in the root directory.
In .env, add the following:
`TOKEN=<your bot token>`
In config.json, add the following:
`{
  "guildID": "<your guild ID>",
  "ownerID": "<your user ID>",
  "prefix": "<prefered prefix, defaults to !>"
}`

## Dependency rules

Bot depends on features, which depend on framework and persistence, which depend on core respectively.

```
bot  ──►  features/*  ──►  framework  ──►  core
                       └─►  persistence ──►  core
```

- `core` depends on nothing. No Kord or database.
- `persistence` depends on `core` only.
- `framework` depends on `core` and `persistence`. Never on a feature.
- `features/*` depend on the three above. **Never on each other.**
- `bot` depends on `framework`, and on features.

If two features need to share code, push it down into `core` or
`framework`. Do not add a feature-to-feature dependency.