# Omepikya Command Center

Omepikya Command Center is an Android command-and-control assistant that accepts natural-language text and voice commands and routes them through a safety-aware execution pipeline.

## Current architecture

The current codebase is a unified Phase 9 baseline, not the old Phase 1-only implementation described by earlier documentation.

**Command pipeline**

Input → normalization → alias/context resolution → intent intelligence → confidence gate → safety gate → planning/workflow → execution coordinator → verification/recovery → learning/context update.

**Autonomous pipeline**

Goal → decomposition → bounded autonomous plan → CommandBrain execution → step verification → persistence → recovery/replanning → completion.

### Major subsystems

- `core` — command orchestration and command results
- `nlp` — intent parsing and entity extraction
- `intelligence` — normalization, confidence, learning, entity resolution and proactive behavior
- `context` — short-lived command context
- `memory` — persistent memory, conversation memory and preferences
- `planning` — multi-step command planning
- `execution` — execution coordination, history, tracing and recovery
- `router` — action registration and dispatch
- `bridge` — Android/system integration
- `security` — confirmation and sensitive-action protection
- `autonomous` — autonomous goals, execution and replanning
- `automation` — workflows and scheduled automation foundations
- `plugins` — plugin runtime and event bus
- `voice` — speech recognition/result handling and text-to-speech integration
- `ui` — command, voice and settings screens

## Supported command categories

The current intent layer includes support for application opening/closing, Android settings navigation, media, communication, automation, navigation, device actions and information requests, with additional entities and actions implemented by the router.

Examples:

- `Open YouTube`
- `Open Wi-Fi settings`
- `Open Bluetooth settings`
- `Open display settings`
- `Send a WhatsApp message to Rahul saying hello`
- `Call Rahul`
- `Run workflow morning routine`

Availability of an action still depends on Android permissions, platform restrictions and the implementation of the corresponding action.

## Safety model

Sensitive operations are not intended to run silently. The command pipeline includes confidence and safety gates, confirmation handling, execution tracing and bounded recovery. Android platform restrictions are respected rather than bypassed.

## Build requirements

The project has been modernized to:

- Android Gradle Plugin `7.4.2`
- Gradle `7.5`
- compile/target SDK `34`
- AndroidX + Material dependencies
- Java 8 source/target compatibility

AGP 7.4.2 itself requires a modern JDK (JDK 11 is the supported runtime). Java 8 remains the source/target language level for compatibility with the existing codebase.

## Verification

Pure NLP coverage is provided under `app/src/test`. The parser tests cover application intents, system-settings intents, empty input and WhatsApp message entity extraction.

Run the standard checks from the project root:

    ./gradlew test
    ./gradlew assembleDebug

For release validation, also run:

    ./gradlew assembleRelease

## Refactoring direction

`CommandBrain` remains the orchestration boundary, but reusable parsing/formatting/context helper logic has been extracted to `CommandBrainSupport`. New capabilities should be implemented in their subsystem rather than adding action-specific logic to the brain itself.

Keep the dependency direction clear:

UI/Voice → CommandBrain → Intelligence/Planning/Safety → ExecutionCoordinator → ActionRouter → Actions/SystemBridge.

Autonomous execution should continue to route generated steps through `CommandBrain` so the same safety, confidence and execution lifecycle applies to both direct and autonomous commands.

## Project status

The repository is now maintained as the modernized Command Center baseline. The next development work should focus on automated test coverage around execution/safety/autonomous flows and further extraction of domain-specific orchestration from `CommandBrain`, rather than adding another large monolithic layer.
