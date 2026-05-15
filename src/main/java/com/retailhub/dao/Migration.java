package com.retailhub.dao;

import com.retailhub.util.SecurityUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Migration {

    public static void inicializar() {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Criar Tabelas
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "login TEXT UNIQUE NOT NULL, " +
                "senha TEXT NOT NULL, " +
                "perfil TEXT NOT NULL, " +
                "status_ativo INTEGER DEFAULT 1, " +
                "data_ultima_senha TEXT DEFAULT CURRENT_DATE);");

            stmt.execute("CREATE TABLE IF NOT EXISTS categorias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "descricao TEXT, " +
                "status_ativo INTEGER DEFAULT 1, " +
                "taxa_comissao REAL DEFAULT 0.0, " +
                "prazo_envio INTEGER DEFAULT 0, " +
                "data_cadastro TEXT DEFAULT CURRENT_DATE);");

            // Tenta adicionar novas colunas se elas não existirem
            try { stmt.execute("ALTER TABLE categorias ADD COLUMN taxa_comissao REAL DEFAULT 0.0;"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE categorias ADD COLUMN prazo_envio INTEGER DEFAULT 0;"); } catch (Exception e) {}

            stmt.execute("CREATE TABLE IF NOT EXISTS cupons (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "codigo TEXT UNIQUE NOT NULL, " +
                "desconto_percentual REAL NOT NULL, " +
                "data_validade TEXT, " +
                "status_ativo INTEGER DEFAULT 1);");

            stmt.execute("CREATE TABLE IF NOT EXISTS produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "sku TEXT UNIQUE, " +
                "descricao TEXT, " +
                "preco REAL NOT NULL, " +
                "preco_costo REAL DEFAULT 0.0, " +
                "categoria_id INTEGER NOT NULL, " +
                "tipo_produto TEXT DEFAULT 'FISICO', " +
                "peso REAL DEFAULT 0.0, " +
                "frete REAL DEFAULT 0.0, " +
                "quantidade_estoque INTEGER DEFAULT 0, " +
                "imagem_path TEXT, " +
                "FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE);");

            // Tenta adicionar novas colunas se elas não existirem
            try { stmt.execute("ALTER TABLE vendas ADD COLUMN status_logistica TEXT DEFAULT 'A Enviar';"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE produtos ADD COLUMN sku TEXT UNIQUE;"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE produtos ADD COLUMN imagem_path TEXT;"); } catch (Exception e) {}

            stmt.execute("CREATE TABLE IF NOT EXISTS clientes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "cpf TEXT UNIQUE, " +
                "email TEXT, " +
                "telefone TEXT, " +
                "endereco TEXT, " +
                "data_cadastro TEXT DEFAULT CURRENT_DATE);");

            stmt.execute("CREATE TABLE IF NOT EXISTS vendas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cliente_id INTEGER, " +
                "usuario_id INTEGER, " +
                "cupom_id INTEGER, " +
                "data_venda TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "status_venda TEXT DEFAULT 'CONCLUIDA', " +
                "status_logistica TEXT DEFAULT 'A Enviar', " +
                "metodo_pagamento TEXT DEFAULT 'DINHEIRO', " +
                "FOREIGN KEY (cliente_id) REFERENCES clientes(id), " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id), " +
                "FOREIGN KEY (cupom_id) REFERENCES cupons(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS itens_venda (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "venda_id INTEGER NOT NULL, " +
                "produto_id INTEGER NOT NULL, " +
                "quantidade INTEGER NOT NULL, " +
                "preco_unitario REAL NOT NULL, " +
                "FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (produto_id) REFERENCES produtos(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS configuracoes (" +
                "chave TEXT PRIMARY KEY, " +
                "valor TEXT);");
            
            // Dados Iniciais da Loja se estiver vazio
            ResultSet rsConf = stmt.executeQuery("SELECT count(*) FROM configuracoes");
            if (rsConf.next() && rsConf.getInt(1) == 0) {
                stmt.execute("INSERT INTO configuracoes (chave, valor) VALUES ('loja_nome', 'RetailHub Store');");
                stmt.execute("INSERT INTO configuracoes (chave, valor) VALUES ('loja_cnpj', '00.000.000/0001-00');");
                stmt.execute("INSERT INTO configuracoes (chave, valor) VALUES ('loja_endereco', 'Rua do Comércio, 123');");
                stmt.execute("INSERT INTO configuracoes (chave, valor) VALUES ('loja_telefone', '(11) 99999-9999');");
                stmt.execute("INSERT INTO configuracoes (chave, valor) VALUES ('loja_tema', 'Light');");
            }

            // 2. Inserir Dados Iniciais se estiver vazio
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM usuarios");
            if (rs.next() && rs.getInt(1) == 0) {
                String senhaAdmin = SecurityUtils.hashPassword("admin123");
                String senhaAfiliado = SecurityUtils.hashPassword("123");
                
                // Admin com data antiga para forçar troca
                stmt.execute("INSERT INTO usuarios (login, senha, perfil, data_ultima_senha) VALUES ('admin', '" + senhaAdmin + "', 'ADMIN', '2020-01-01');");
                stmt.execute("INSERT INTO usuarios (login, senha, perfil, data_ultima_senha) VALUES ('afiliado', '" + senhaAfiliado + "', 'AFILIADO', '2026-01-01');");
                
                // Categorias e Produtos Iniciais
                stmt.execute("INSERT INTO categorias (nome, descricao) VALUES ('Eletrônicos', 'Dispositivos em geral');");
                stmt.execute("INSERT INTO categorias (nome, descricao) VALUES ('Cursos Digitais', 'E-books e vídeos');");
                
                stmt.execute("INSERT INTO produtos (nome, preco, categoria_id, tipo_produto, quantidade_estoque) VALUES ('Smartphone Pro', 2500.0, 1, 'FISICO', 10);");
                stmt.execute("INSERT INTO produtos (nome, preco, categoria_id, tipo_produto, quantidade_estoque) VALUES ('Curso Java OO', 99.90, 2, 'DIGITAL', 999);");
                
                stmt.execute("INSERT INTO cupons (codigo, desconto_percentual) VALUES ('BEMVINDO10', 10.0);");
            }

        } catch (Exception e) {
            System.err.println("Erro na migração/inicialização: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        inicializar();
        System.out.println("Banco de dados inicializado com sucesso.");
    }
}
