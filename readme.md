# CSX KeyAll

<div align="center">

![CSX KeyAll](https://img.shields.io/badge/CSX-KeyAll-1.0.1-brightgreen?style=for-the-badge)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.x-orange?style=for-the-badge\&logo=minecraft)
![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge\&logo=openjdk)
![License](https://img.shields.io/github/license/COSAX-ID/CSX-Keyall?style=for-the-badge)
![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur%20%7C%20Spigot-success?style=for-the-badge)
![Database](https://img.shields.io/badge/Database-H2%20%7C%20MySQL-blue?style=for-the-badge)

### High-Performance Automated KeyAll System for Minecraft Servers

Lightweight, modern, and database-safe KeyAll scheduler designed for Paper-based Minecraft servers.

Built with asynchronous processing, PlaceholderAPI integration, multi-language support, countdown persistence, and optimized database handling.

[Features](#-features) • [Requirements](#-requirements) • [Installation](#-installation) • [Configuration](#️-configuration) • [Commands](#-commands) • [Permissions](#-permissions) • [Building](#️-building-from-source) • [Troubleshooting](#-troubleshooting) • [License](#-license)

</div>

---

# ✨ Features

## ⏰ Automated Countdown System

* Fully automated KeyAll execution
* Persistent countdown storage
* Crash-safe timer recovery
* Millisecond precision timing
* Manual execution support

## 🗄 Database Support

| Database    | Supported |
| ----------- | --------- |
| H2          | ✅         |
| MySQL       | ✅         |

### Database Features

* HikariCP Connection Pooling (MySQL)
* H2 JDBC Native Driver
* Automatic Storage Detection
* Countdown Persistence Across Restarts

## 🌎 Localization

* Multi-language Support
* Indonesian Language Included
* English Language Included
* Fully Customizable Messages

## 🔌 PlaceholderAPI Integration

Available Placeholders:

```text
%csxkeyall_time%
%csxkeyall_seconds%
%csxkeyall_minutes%
```

Compatible With:

* TAB
* Scoreboards
* Holograms
* ActionBars
* BossBars

---

# 📋 Requirements

| Software       | Version   |
| -------------- | --------- |
| Minecraft      | 1.21.x    |
| Java           | 21+       |
| Paper          | Supported |
| Purpur         | Supported |
| Spigot         | Supported |
| PlaceholderAPI | Optional  |

---

# 📥 Installation

### 1. Download Plugin

Place:

```text
csx-keyall-1.0.1.jar
```

inside:

```text
/plugins/
```

### 2. Start Server

Start the server once.

The plugin will automatically generate:

```text
/plugins/CSX-Keyall/
```

### 3. Configure Plugin

Edit:

```text
config.yml
```

Configure:

* Database
* Commands
* Language
* Interval

### 4. Reload

```bash
/keyall reload
```

or restart the server.

---

# ⚙️ Configuration

Example:

```yaml
database:
  type: "H2"

  host: "localhost"
  port: 3306
  name: "minecraft"

  username: "root"
  password: ""

  ssl: false

lang: "id"

interval-millis: 3600000

commands-list:
  - "crate giveall vote 1"
  - "bc &b&lCSXID &7» &aEveryone has received 1x Vote Key!"
```

---

# 📖 Commands

| Command               | Description              | Permission   |
| --------------------- | ------------------------ | ------------ |
| /keyall info          | View remaining countdown | keyall.use   |
| /keyall reset         | Reset countdown          | keyall.admin |
| /keyall set-time <ms> | Modify timer             | keyall.admin |
| /keyall cmd           | Force execute KeyAll     | keyall.admin |
| /keyall reload        | Reload configuration     | keyall.admin |

---

# 🔐 Permissions

| Permission   | Default | Description            |
| ------------ | ------- | ---------------------- |
| keyall.use   | true    | Access player commands |
| keyall.admin | op      | Access admin commands  |

---

# 🛠️ Building From Source

### Clone Repository

```bash
git clone https://github.com/COSAX-ID/CSX-Keyall.git
cd CSX-Keyall
```

### Build

```bash
mvn clean package
```

Output:

```text
target/csx-keyall-1.0.1.jar
```

---

# 📂 Project Structure

```text
CSX-KeyAll/
├── changelog/
│   └── 1.0.1.md
├── src/
│   └── main/
│       ├── java/
│       │   └── dev/cosax/csxkeyall/
│       └── resources/
│           ├── plugin.yml
│           ├── config.yml
│           └── lang/
│               ├── en.yml
│               └── id.yml
├── pom.xml
└── README.md
```

---

# 🔧 Troubleshooting

## PlaceholderAPI Not Working

Make sure:

* PlaceholderAPI is installed
* Server has been restarted
* Expansions are loaded correctly

## MySQL Connection Failed

Check:

* Host
* Port
* Username
* Password
* Firewall Settings

## Countdown Reset After Restart

Ensure the plugin has permission to write data inside:

```text
/plugins/CSX-KeyAll/
```

---

# 👨‍💻 Credits

## Developed By

| Role           | Name      |
| -------------- | --------- |
| Organization   | COSAX.ID  |
| Lead Developer | COSAXID   |
| Developer      | minggudev |

### GitHub

* https://github.com/COSAX-ID
* https://github.com/minggudevv

---

# 📜 License

This project is licensed under the MIT License.

For more information, see the [LICENSE](LICENSE) file.

---

# ⭐ Support

If you enjoy using CSX KeyAll, please consider giving this repository a star.

Your support helps future development and maintenance.

<div align="center">

Made with ❤️ by COSAX.ID

### ⬆ Back To Top

[Click Here](#csx-keyall)

</div>
