# ❄️ Gweneira Discord Bot

Gweneira is a simple, beginner-friendly Discord bot built in **Java** using the [JDA](https://github.com/discord-jda/JDA) library.  
This project is designed to be easy to set up, extend, and learn from.

---

## ✨ Features
- Example-based configuration via XML (no hardcoded tokens).
- Uses Gradle with the official JDA dependency.
- Lightweight, only the essentials included.

---

## 💫 Future Ideas
- Customizable command prefix
- Rich embed responses
- Slash command support
- Configurable links and activities from XML

---


## 🚀 For people who wants to try it themselves

### 📦 Requirements
- [Java 17+](https://adoptium.net/)
- [Gradle](https://gradle.org/) (or just use the included `gradlew` wrapper)
- A [Discord Application & Bot Token](https://discord.com/developers/applications)

### Clone the repository
```bash
git clone https://github.com/manhanbanhcanh/gweneira.git
cd gweneira
```

### Configure your bot token
- Copy the example configuration file:
```bash
cp src/main/resources/config.example.xml src/main/resources/config.xml
```
- Open `src/main/resource/config.xml` and replace the placeholder with your bot token:
```xml
<config>
    <token>YOUR-BOT-TOKEN-HERE</token>
</config>
```
> 🚫 Please don't commit your real `config.xml` (it's already ignored in `.gitignore`)

### Run the bot
- Using Gradle wrapper:
```bash
./gradlew run
```
or (on Windows):
```bash
gradlew.bat run
```

If you have done everything correctly, your bot should come online and display its status ``Playing with Gweneira ❄️``



