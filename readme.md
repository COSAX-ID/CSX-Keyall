# CSX KeyAll 🗝️

<div align="center">

![CSX KeyAll](https://img.shields.io/badge/CSX-KeyAll-1.0.0-brightgreen?style=for-the-badge)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-orange?style=for-the-badge\&logo=minecraft)
![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge\&logo=openjdk)
![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge)

### High-performance automated KeyAll reward system for Minecraft servers

Lightweight, modern, and database-safe reward scheduler built for Paper-based servers with multi-language support, countdown persistence, PlaceholderAPI integration, and optimized async handling.

[Features](#-features) • [Installation](#-installation) • [Usage](#-usage) • [Configuration](#️-configuration) • [Permissions](#-permissions) • [Commands](#-commands) • [Building](#️-building-from-source) • [Troubleshooting](#-troubleshooting) • [License](#-license)

</div>

---

# 🌟 Features

<details>
<summary><strong>Expand Features List</strong></summary>

---

## ⏱️ Automated Reward System

* Automated countdown scheduling system
* Precise interval timing using milliseconds
* Persistent countdown saving
* Crash-safe timer restoration
* Manual admin trigger support

---

## 🗄️ Modern Database Layer

### Supported Storage Engines

| Database           | Support |
| ------------------ | ------- |
| SQLite             | ✅       |
| MySQL              | ✅       |
| H2 Legacy Fallback | ✅       |

### Features

* HikariCP connection pooling
* Smart storage bridge system
* Async database operations
* Paper-compatible SQLite handling
* Automatic H2 → SQLite fallback conversion

---

## 🎨 Customization & Localization

* Multi-language support
* Built-in Indonesian & English locale
* Fully customizable messages
* PlaceholderAPI integration
* Async-safe scheduler system

---

## 🔌 PlaceholderAPI Support

Available placeholders:

```text
%csxkeyall_time%
%csxkeyall_seconds%
%csxkeyall_minutes%
```

Use placeholders on:

* Scoreboards
* TAB
* Holograms
* ActionBars
* BossBars

</details>

---

# 📋 Requirements

| Requirement     | Version                 |
| --------------- | ----------------------- |
| Minecraft       | 1.21.x                  |
| Java            | 21+                     |
| Server Software | Paper / Purpur / Spigot |
| Optional Plugin | PlaceholderAPI          |

---

# 📥 Installation

## Quick Start

### 1. Download Plugin

Place:

```text
csx-keyall-1.0.jar
```

into your server:

```text
/plugins/
```

---

### 2. Start Server

Start the server once to generate:

```text
/plugins/CSX-Keyall/
```

---

### 3. Configure Plugin

Edit:

```text
config.yml
```

Setup:

* Database
* Reward commands
* Language
* Countdown interval

---

### 4. Reload Plugin

Use:

```bash
/keyall reload
```

or restart the server.

---

# 🚀 Usage

## 👥 Player Commands

| Command        | Description              |
| -------------- | ------------------------ |
| `/keyall info` | View remaining countdown |

---

## 🛠️ Admin Features

* Force execute KeyAll
* Reset countdown
* Reload configuration
* Modify timer dynamically

---

# ⚙️ Configuration

## Example `config.yml`

```yaml
# ==================================================
# CSX-KeyAll Configuration
# ==================================================

database:
  # Supported:
  # SQLITE
  # MYSQL
  type: "SQLITE"

  host: "localhost"
  port: 3306
  name: "minecraft"

  username: "root"
  password: ""

  ssl: false

# Language file
# lang/en.yml
# lang/id.yml
lang: "id"

# 1 Hour = 3600000
interval-millis: 3600000

# Commands executed by console
commands-list:
  - "crate giveall vote 1"
  - "bc &b&lCSXID &7» &aEveryone has received 1x Vote Key!"
```

---

# 🔐 Permissions

| Permission     | Default | Description           |
| -------------- | ------- | --------------------- |
| `keyall.use`   | true    | Access `/keyall info` |
| `keyall.admin` | op      | Full admin access     |

---

# 📜 Commands

| Command                 | Description           | Permission     |
| ----------------------- | --------------------- | -------------- |
| `/keyall info`          | Show countdown info   | `keyall.use`   |
| `/keyall reset`         | Reset timer           | `keyall.admin` |
| `/keyall set-time <ms>` | Modify remaining time | `keyall.admin` |
| `/keyall cmd`           | Force trigger KeyAll  | `keyall.admin` |
| `/keyall reload`        | Reload plugin config  | `keyall.admin` |

---

# 🏗️ Building from Source

## Requirements

* Java 21+
* Maven 3.6+

---

## Clone Repository

```bash
git clone https://github.com/minggudevv/CSX-Keyall.git
cd CSX-Keyall
```

---

## Build Plugin

```bash
mvn clean package
```

Output:

```text
target/csx-keyall-1.0-SNAPSHOT.jar
```

---

# 📁 Project Structure

```text
CSXKeyall/
├── src/main/java/dev/cosax/csxkeyall/
│   ├── CSXKeyall.java
│   ├── DatabaseManager.java
│   ├── CommandHandler.java
│   ├── TimerTask.java
│   └── config/
│       ├── ConfigLoader.java
│       └── LanguageManager.java
│
└── src/main/resources/
    ├── plugin.yml
    ├── config.yml
    └── lang/
        ├── en.yml
        └── id.yml
```

---

# 🐛 Troubleshooting

## H2 Warning Appears

### Problem

Paper 1.20+ no longer bundles H2 database driver.

### Solution

CSX-KeyAll automatically converts H2 configurations into SQLite safely.

---

## PlaceholderAPI Not Working

### Problem

Placeholders return raw text.

### Solution

* Ensure PlaceholderAPI is installed
* Reload expansions
* Restart server if needed

---

## MySQL Timeout Issues

### Problem

Database connection becomes unstable.

### Solution

* Verify MySQL credentials
* Check firewall port 3306
* Ensure remote access is enabled

---

# 📄 License

Licensed under the Apache License 2.0.

```text
Copyright 2026 cosaxid

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

http://www.apache.org/licenses/LICENSE-2.0
```

---

# 👨‍💻 Credits

## Development

| Role         | Information                   |
| ------------ | ----------------------------- |
| Author       | cosaxid                       |
| Organization | COSAX.ID                      |
| GitHub       | https://github.com/minggudevv |

---

# ⭐ Support

If you like this plugin, consider starring the repository on GitHub.

Made with ❤️ for the Minecraft community.

<div align="center">

## ⬆ Back To Top

[Click Here](#csx-keyall-️)

</div>
