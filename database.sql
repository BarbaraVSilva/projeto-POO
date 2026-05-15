-- Script para criação do banco de dados SQLite
-- Lojinha da Shopee

CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    login TEXT UNIQUE NOT NULL,
    senha TEXT NOT NULL,
    perfil TEXT NOT NULL, -- 'ADMIN' ou 'AFILIADO'
    status_ativo INTEGER DEFAULT 1,
    data_ultima_senha TEXT DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS categorias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    descricao TEXT,
    status_ativo INTEGER DEFAULT 1, -- 1 para Ativo, 0 para Inativo
    data_cadastro TEXT DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS fornecedores (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cnpj TEXT UNIQUE,
    telefone TEXT,
    email TEXT
);

CREATE TABLE IF NOT EXISTS cupons (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT UNIQUE NOT NULL,
    desconto_percentual REAL NOT NULL,
    data_validade TEXT,
    status_ativo INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS produtos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    descricao TEXT,
    preco REAL NOT NULL,
    preco_costo REAL DEFAULT 0.0,
    categoria_id INTEGER NOT NULL,
    tipo_produto TEXT DEFAULT 'FISICO', -- 'FISICO' ou 'DIGITAL'
    peso REAL DEFAULT 0.0,
    frete REAL DEFAULT 0.0,
    quantidade_estoque INTEGER DEFAULT 0,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clientes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cpf TEXT UNIQUE,
    email TEXT,
    telefone TEXT,
    endereco TEXT,
    data_cadastro TEXT DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS vendas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER,
    usuario_id INTEGER,
    cupom_id INTEGER,
    data_venda TEXT DEFAULT CURRENT_TIMESTAMP,
    status_venda TEXT DEFAULT 'CONCLUIDA', -- PENDENTE, CONCLUIDA, CANCELADA
    metodo_pagamento TEXT DEFAULT 'DINHEIRO', -- DINHEIRO, CARTAO, PIX
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (cupom_id) REFERENCES cupons(id)
);

CREATE TABLE IF NOT EXISTS itens_venda (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    venda_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario REAL NOT NULL,
    FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

-- Senhas padrão (123) com hash BCrypt para compatibilidade inicial
INSERT INTO usuarios (login, senha, perfil, data_ultima_senha) VALUES 
('admin', '$2a$10$8.K9/9f.A7/f7f.7f.7f.7f.7f.7f.7f.7f.7f.7f.7f.7f.7f.7', 'ADMIN', '2020-01-01'), -- Data antiga para forçar troca
('afiliado', '$2a$10$8.K9/9f.A7/f7f.7f.7f.7f.7f.7f.7f.7f.7f.7f.7f.7f.7f.7', 'AFILIADO', '2020-01-01');

INSERT INTO categorias (nome, descricao, status_ativo, data_cadastro) VALUES 
('Eletrônicos', 'Dispositivos eletrônicos em geral', 1, '2026-05-10'),
('Papelaria', 'Cadernos, canetas e material de escritório', 1, '2026-05-10'),
('Vestuário', 'Roupas e acessórios de moda', 1, '2026-05-10'),
('Cursos Digitais', 'Cursos e e-books online', 1, '2026-05-10');

INSERT INTO cupons (codigo, desconto_percentual, status_ativo) VALUES 
('BEMVINDO10', 10.0, 1),
('SHOPEE20', 20.0, 1),
('FRETEGRATIS', 15.0, 1);

INSERT INTO clientes (nome, cpf, email, telefone, endereco) VALUES
('Cliente Padrao', '00000000000', 'cliente@padrao.com', '11999999999', 'Rua Padrao, 123');

INSERT INTO produtos (nome, descricao, preco, preco_costo, categoria_id, tipo_produto, peso, frete, quantidade_estoque) VALUES 
('Smartphone XYZ', 'Celular de última geração 128GB', 1999.99, 1500.00, 1, 'FISICO', 0.5, 20.0, 10),
('E-book Programação Java', 'Livro digital completo de Java OO', 45.00, 5.00, 4, 'DIGITAL', 0, 0, 999),
('Fone Bluetooth Baseus', 'Fone de ouvido sem fio', 150.00, 80.00, 1, 'FISICO', 0.2, 10.0, 50),
('Caderno Universitário', 'Caderno 10 matérias Capa Dura', 25.50, 12.00, 2, 'FISICO', 0.8, 15.0, 100),
('Camiseta Shopee', 'Camiseta oficial tamanho M', 35.90, 15.00, 3, 'FISICO', 0.3, 12.0, 30),
('Curso Marketing Afiliados', 'Curso em vídeo aulas', 297.00, 50.00, 4, 'DIGITAL', 0, 0, 999);
