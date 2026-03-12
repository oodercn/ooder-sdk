-- ============================================
-- LLM Knowledge Base Schema
-- 版本: 2.3.2
-- 说明: 为RAG功能和会话历史创建数据库表
-- ============================================

-- --------------------------------------------
-- 会话主表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS llm_session (
    session_id VARCHAR(64) PRIMARY KEY COMMENT '会话ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    skill_id VARCHAR(64) NOT NULL COMMENT 'Skill ID',
    title VARCHAR(255) COMMENT '会话标题',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    message_count INT NOT NULL DEFAULT 0 COMMENT '消息数量',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active, archived, deleted',
    INDEX idx_user_id (user_id),
    INDEX idx_skill_id (skill_id),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM会话主表';

-- --------------------------------------------
-- 会话消息表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS llm_session_message (
    message_id VARCHAR(64) PRIMARY KEY COMMENT '消息ID',
    session_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色: user, assistant, system',
    content TEXT NOT NULL COMMENT '消息内容',
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间',
    metadata JSON COMMENT '元数据(JSON格式)',
    INDEX idx_session_id (session_id),
    INDEX idx_timestamp (timestamp),
    FOREIGN KEY (session_id) REFERENCES llm_session(session_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM会话消息表';

-- --------------------------------------------
-- 知识库表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS llm_knowledge_base (
    kb_id VARCHAR(64) PRIMARY KEY COMMENT '知识库ID',
    name VARCHAR(255) NOT NULL COMMENT '知识库名称',
    description TEXT COMMENT '知识库描述',
    owner_id VARCHAR(64) NOT NULL COMMENT '所有者ID',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active, inactive, building',
    index_status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '索引状态: pending, building, ready, failed',
    document_count INT NOT NULL DEFAULT 0 COMMENT '文档数量',
    chunk_count INT NOT NULL DEFAULT 0 COMMENT '分块数量',
    total_size BIGINT NOT NULL DEFAULT 0 COMMENT '总大小(字节)',
    config JSON COMMENT '配置信息(JSON格式)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_owner_id (owner_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM知识库表';

-- --------------------------------------------
-- 知识库文档表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS llm_knowledge_document (
    doc_id VARCHAR(64) PRIMARY KEY COMMENT '文档ID',
    kb_id VARCHAR(64) NOT NULL COMMENT '知识库ID',
    title VARCHAR(255) NOT NULL COMMENT '文档标题',
    source_type VARCHAR(50) NOT NULL COMMENT '来源类型: file, url, inline',
    source_path VARCHAR(1024) COMMENT '来源路径',
    content_hash VARCHAR(64) COMMENT '内容哈希',
    chunk_count INT NOT NULL DEFAULT 0 COMMENT '分块数量',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending, processing, indexed, failed',
    metadata JSON COMMENT '元数据(JSON格式)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_kb_id (kb_id),
    INDEX idx_status (status),
    FOREIGN KEY (kb_id) REFERENCES llm_knowledge_base(kb_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM知识库文档表';

-- --------------------------------------------
-- 知识库分块表(可选,用于元数据管理)
-- 注意: 实际向量数据存储在向量数据库中
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS llm_knowledge_chunk (
    chunk_id VARCHAR(64) PRIMARY KEY COMMENT '分块ID',
    doc_id VARCHAR(64) NOT NULL COMMENT '文档ID',
    kb_id VARCHAR(64) NOT NULL COMMENT '知识库ID',
    chunk_index INT NOT NULL COMMENT '分块索引',
    content_preview VARCHAR(500) COMMENT '内容预览',
    vector_id VARCHAR(64) COMMENT '向量ID(在向量数据库中)',
    token_count INT COMMENT 'Token数量',
    metadata JSON COMMENT '元数据(JSON格式)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_doc_id (doc_id),
    INDEX idx_kb_id (kb_id),
    FOREIGN KEY (doc_id) REFERENCES llm_knowledge_document(doc_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM知识库分块表';

-- --------------------------------------------
-- 知识库权限表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS llm_knowledge_permission (
    permission_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    kb_id VARCHAR(64) NOT NULL COMMENT '知识库ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    permission VARCHAR(20) NOT NULL COMMENT '权限: read, write, admin',
    granted_by VARCHAR(64) COMMENT '授权人',
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    expires_at TIMESTAMP COMMENT '过期时间',
    UNIQUE KEY uk_kb_user (kb_id, user_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (kb_id) REFERENCES llm_knowledge_base(kb_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM知识库权限表';

-- --------------------------------------------
-- 安装记录表
-- --------------------------------------------
CREATE TABLE IF NOT EXISTS skill_install_record (
    install_id VARCHAR(64) PRIMARY KEY COMMENT '安装ID',
    skill_id VARCHAR(64) NOT NULL COMMENT 'Skill ID',
    operator_id VARCHAR(64) NOT NULL COMMENT '操作人ID',
    target_path VARCHAR(1024) COMMENT '目标路径',
    status VARCHAR(20) NOT NULL COMMENT '状态: pending, running, completed, failed',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP COMMENT '结束时间',
    duration_ms BIGINT COMMENT '持续时间(毫秒)',
    knowledge_base_index_id VARCHAR(64) COMMENT '知识库索引ID',
    install_report JSON COMMENT '安装报告(JSON格式)',
    error_message TEXT COMMENT '错误信息',
    INDEX idx_skill_id (skill_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Skill安装记录表';

-- --------------------------------------------
-- 初始化数据
-- --------------------------------------------

-- 插入默认知识库(系统知识库)
INSERT INTO llm_knowledge_base (kb_id, name, description, owner_id, status, index_status)
VALUES ('system-default', 'System Default Knowledge Base', 'Default knowledge base for system', 'system', 'active', 'ready')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- --------------------------------------------
-- 权限注释
-- --------------------------------------------

COMMENT ON TABLE llm_session IS 'LLM会话主表,存储用户与AI的对话会话';
COMMENT ON TABLE llm_session_message IS 'LLM会话消息表,存储会话中的每条消息';
COMMENT ON TABLE llm_knowledge_base IS 'LLM知识库表,存储知识库元数据';
COMMENT ON TABLE llm_knowledge_document IS 'LLM知识库文档表,存储文档元数据';
COMMENT ON TABLE llm_knowledge_chunk IS 'LLM知识库分块表,存储文档分块元数据';
COMMENT ON TABLE llm_knowledge_permission IS 'LLM知识库权限表,存储用户权限';
COMMENT ON TABLE skill_install_record IS 'Skill安装记录表,记录安装历史和状态';
