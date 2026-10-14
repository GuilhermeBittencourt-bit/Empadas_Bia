-- =====================================================================
-- Empadas da Bia - Sistema de Gerenciamento
-- Script de criação do banco de dados (MySQL 8+)
-- Execute este arquivo primeiro e depois o dados_exemplo.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS empadas_da_bia
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

SET NAMES utf8mb4;
USE empadas_da_bia;

-- Remove as tabelas na ordem inversa das dependências (permite rodar o script de novo)
DROP TABLE IF EXISTS item_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS categoria;

-- ---------------------------------------------------------------------
-- CATEGORIA: agrupa os produtos (Empadas Salgadas, Doces, Combos...)
-- ---------------------------------------------------------------------
CREATE TABLE categoria (
    id_categoria INT          NOT NULL AUTO_INCREMENT,
    nome         VARCHAR(50)  NOT NULL,
    descricao    VARCHAR(150) NULL,
    CONSTRAINT pk_categoria PRIMARY KEY (id_categoria),
    CONSTRAINT uk_categoria_nome UNIQUE (nome)
);

-- ---------------------------------------------------------------------
-- PRODUTO: cada empada ou combo vendido
-- imagem guarda apenas o NOME do arquivo (ex.: empada-frango.png)
-- ---------------------------------------------------------------------
CREATE TABLE produto (
    id_produto   INT           NOT NULL AUTO_INCREMENT,
    nome         VARCHAR(100)  NOT NULL,
    descricao    VARCHAR(255)  NULL,
    preco        DECIMAL(10,2) NOT NULL,
    ativo        BOOLEAN       NOT NULL DEFAULT TRUE,
    imagem       VARCHAR(255)  NULL,
    id_categoria INT           NOT NULL,
    CONSTRAINT pk_produto PRIMARY KEY (id_produto),
    CONSTRAINT ck_produto_preco CHECK (preco > 0),
    CONSTRAINT fk_produto_categoria FOREIGN KEY (id_categoria)
        REFERENCES categoria (id_categoria)
        ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- CLIENTE: quem faz os pedidos (o endereço fica na própria tabela)
-- telefone e cep são guardados só com números
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
    id_cliente INT          NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(100) NOT NULL,
    telefone   VARCHAR(15)  NOT NULL,
    email      VARCHAR(100) NULL,
    cep        CHAR(8)      NULL,
    logradouro VARCHAR(120) NULL,
    numero     VARCHAR(10)  NULL,
    bairro     VARCHAR(60)  NULL,
    cidade     VARCHAR(60)  NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id_cliente)
);

-- ---------------------------------------------------------------------
-- PEDIDO: cabeçalho do pedido (quem pediu, quando e situação)
-- O total NÃO é guardado: é calculado a partir dos itens.
-- ---------------------------------------------------------------------
CREATE TABLE pedido (
    id_pedido   INT          NOT NULL AUTO_INCREMENT,
    id_cliente  INT          NOT NULL,
    data_pedido DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      ENUM('PENDENTE', 'ENTREGUE', 'CANCELADO') NOT NULL DEFAULT 'PENDENTE',
    observacao  VARCHAR(255) NULL,
    CONSTRAINT pk_pedido PRIMARY KEY (id_pedido),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente (id_cliente)
        ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- ITEM_PEDIDO: resolve o relacionamento N:N entre pedido e produto.
-- preco_unitario guarda o preço NO MOMENTO da venda.
-- ---------------------------------------------------------------------
CREATE TABLE item_pedido (
    id_item        INT           NOT NULL AUTO_INCREMENT,
    id_pedido      INT           NOT NULL,
    id_produto     INT           NOT NULL,
    quantidade     INT           NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_item_pedido PRIMARY KEY (id_item),
    CONSTRAINT ck_item_quantidade CHECK (quantidade > 0),
    CONSTRAINT fk_item_pedido FOREIGN KEY (id_pedido)
        REFERENCES pedido (id_pedido)
        ON DELETE CASCADE,
    CONSTRAINT fk_item_produto FOREIGN KEY (id_produto)
        REFERENCES produto (id_produto)
        ON DELETE RESTRICT
);
