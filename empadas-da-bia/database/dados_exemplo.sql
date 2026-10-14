-- =====================================================================
-- Empadas da Bia - Dados de exemplo para testes e apresentação
-- Execute depois do schema.sql
-- As datas dos pedidos são relativas ao momento da execução,
-- assim o Dashboard sempre mostra informações "de hoje".
-- =====================================================================

SET NAMES utf8mb4;
USE empadas_da_bia;

INSERT INTO categoria (id_categoria, nome, descricao) VALUES
(1, 'Empadas Salgadas', 'Empadas com recheios salgados'),
(2, 'Empadas Doces',    'Empadas com recheios doces'),
(3, 'Combos',           'Kits com várias empadas'),
(4, 'Outros',           'Bebidas e demais itens');

INSERT INTO produto (id_produto, nome, descricao, preco, ativo, imagem, id_categoria) VALUES
(1, 'Empada de Frango',              'Frango desfiado temperado com cheiro-verde.',  8.00, TRUE,  'empada-frango.png',          1),
(2, 'Empada de Frango com Catupiry', 'Frango desfiado com o cremoso catupiry.',      9.00, TRUE,  'empada-frango-catupiry.png', 1),
(3, 'Empada de Palmito',             'Palmito macio em molho branco.',               8.50, TRUE,  'empada-palmito.png',         1),
(4, 'Empada de Bacon',               'Bacon crocante com queijo.',                   9.00, TRUE,  'empada-bacon.png',           1),
(5, 'Empada de Camarão',             'Camarão refogado com tomate e temperos.',     12.00, TRUE,  'empada-camarao.png',         1),
(6, 'Empada Doce de Leite',          'Massa doce recheada com doce de leite.',       9.00, TRUE,  'empada-doce-de-leite.png',   2),
(7, 'Empada de Chocolate',           'Recheio cremoso de chocolate com granulado.',  9.00, TRUE,  'empada-chocolate.png',       2),
(8, 'Empada Romeu e Julieta',        'Queijo minas com goiabada.',                   9.50, FALSE, 'empada-romeu-julieta.png',   2),
(9, 'Combo Família (10 unidades)',   '10 empadas à escolha entre salgadas e doces.', 80.00, TRUE, 'combo-familia.png',          3);

INSERT INTO cliente (id_cliente, nome, telefone, email, cep, logradouro, numero, bairro, cidade) VALUES
(1, 'Ana Souza',      '47998123344', 'ana.souza@email.com',     '89201000', 'Rua das Palmeiras',     '120', 'Centro',        'Joinville'),
(2, 'Carlos Mendes',  '47996542210', 'carlos.m@email.com',      '89204000', 'Rua Max Colin',         '455', 'América',       'Joinville'),
(3, 'Juliana Rocha',  '47991338890', 'juliana.rocha@email.com', '89216100', 'Rua Ottokar Doerffel',  '980', 'Glória',        'Joinville'),
(4, 'Pedro Alves',    '47988771020', NULL,                      '89222000', 'Rua Tuiuti',            '77',  'Bom Retiro',    'Joinville'),
(5, 'Mariana Lima',   '47997024455', 'mari.lima@email.com',     '89220000', 'Rua Iririú',            '310', 'Costa e Silva', 'Joinville'),
(6, 'Rafael Costa',   '47992887731', 'rafael.costa@email.com',  '89210000', 'Rua Monsenhor Gercino', '1500','Itaum',         'Joinville'),
(7, 'Beatriz Nunes',  '47995416602', 'bia.nunes@email.com',     '89245000', 'Rua XV de Novembro',    '33',  'Centro',        'Araquari'),
(8, 'Lucas Ferreira', '47993601188', NULL,                      '89221000', 'Rua Blumenau',          '845', 'Saguaçu',       'Joinville');

INSERT INTO pedido (id_pedido, id_cliente, data_pedido, status, observacao) VALUES
(1,  2, DATE_SUB(NOW(), INTERVAL 9 DAY),      'ENTREGUE',  NULL),
(2,  3, DATE_SUB(NOW(), INTERVAL 8 DAY),      'ENTREGUE',  NULL),
(3,  5, DATE_SUB(NOW(), INTERVAL 7 DAY),      'ENTREGUE',  'Festa de aniversário'),
(4,  1, DATE_SUB(NOW(), INTERVAL 6 DAY),      'ENTREGUE',  NULL),
(5,  6, DATE_SUB(NOW(), INTERVAL 5 DAY),      'CANCELADO', 'Cliente desistiu'),
(6,  8, DATE_SUB(NOW(), INTERVAL 4 DAY),      'ENTREGUE',  NULL),
(7,  4, DATE_SUB(NOW(), INTERVAL 3 DAY),      'ENTREGUE',  NULL),
(8,  7, DATE_SUB(NOW(), INTERVAL 2 DAY),      'ENTREGUE',  'Retirada no balcão'),
(9,  1, DATE_SUB(NOW(), INTERVAL 2 DAY),      'ENTREGUE',  NULL),
(10, 6, DATE_SUB(NOW(), INTERVAL 1 DAY),      'ENTREGUE',  NULL),
(11, 5, DATE_SUB(NOW(), INTERVAL 1 DAY),      'CANCELADO', NULL),
(12, 4, DATE_SUB(NOW(), INTERVAL 1 DAY),      'ENTREGUE',  NULL),
(13, 3, DATE_SUB(NOW(), INTERVAL 90 MINUTE),  'ENTREGUE',  NULL),
(14, 2, DATE_SUB(NOW(), INTERVAL 60 MINUTE),  'PENDENTE',  NULL),
(15, 1, DATE_SUB(NOW(), INTERVAL 30 MINUTE),  'PENDENTE',  'Entregar após as 18h');

INSERT INTO item_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES
(1, 1, 6, 8.00),  (1, 6, 4, 9.00),
(2, 2, 5, 9.00),  (2, 4, 5, 9.00),
(3, 9, 1, 80.00),
(4, 1, 10, 8.00), (4, 2, 10, 9.00),
(5, 5, 4, 12.00),
(6, 1, 8, 8.00),  (6, 7, 4, 9.00),
(7, 2, 6, 9.00),  (7, 6, 6, 9.00),
(8, 9, 2, 80.00),
(9, 1, 5, 8.00),
(10, 3, 4, 8.50), (10, 4, 4, 9.00),
(11, 6, 3, 9.00), (11, 7, 3, 9.00),
(12, 1, 2, 8.00), (12, 4, 2, 9.00),
(13, 9, 1, 80.00),
(14, 1, 6, 8.00), (14, 2, 6, 9.00),
(15, 1, 3, 8.00), (15, 3, 2, 8.50), (15, 6, 2, 9.00);
