#!/usr/bin/env python3
"""
Skills Framework Migration Script
迁移 api/skill/, core/skill/, skill/ 到 skills/ 包下
"""

import os
import re
import shutil
from pathlib import Path

# 配置
BASE_DIR = Path("e:/github/ooder-sdk/agent-sdk/src/main/java/net/ooder/sdk")

# 迁移映射: (源包, 目标包)
MIGRATIONS = [
    ("api/skill", "skills/api"),
    ("core/skill", "skills/core"),
    ("skill", "skills/md"),
]

# 包名映射: 用于更新import语句
PACKAGE_MAPPINGS = {
    "net.ooder.sdk.api.skill": "net.ooder.sdk.skills.api",
    "net.ooder.sdk.core.skill": "net.ooder.sdk.skills.core",
    "net.ooder.sdk.skill": "net.ooder.sdk.skills.md",
}

def migrate_package(src_rel