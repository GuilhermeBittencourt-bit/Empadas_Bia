# Roteiro de testes manuais

Execute `database/schema.sql` e `database/dados_exemplo.sql` antes de começar.

| # | Tela | Ação | Resultado esperado |
|---|---|---|---|
| 1 | Início | Abrir o sistema | Cards com números, últimos 5 pedidos e as 3 mais pedidas da semana |
| 2 | Início | Desligar o MySQL e abrir o sistema | Mensagem explicando como corrigir a conexão |
| 3 | Categorias | Salvar com o nome vazio | Aviso "O campo Nome é obrigatório" |
| 4 | Categorias | Cadastrar "Empadas Doces" novamente | Aviso de nome duplicado |
| 5 | Categorias | Cadastrar "Bebidas", clicar na linha, editar e salvar | Categoria criada e depois alterada |
| 6 | Categorias | Excluir "Empadas Salgadas" | Bloqueado: a categoria possui produtos |
| 7 | Categorias | Excluir a categoria "Bebidas" (sem produtos) | Excluída |
| 8 | Produtos | Filtrar por "Empadas Doces" e buscar "choc" | Apenas a Empada de Chocolate |
| 9 | Produtos | Novo produto com preço "abc" ou "0" | Aviso de preço inválido |
| 10 | Produtos | Novo produto com foto escolhida em "Escolher imagem…" | Cartão aparece no cardápio com a foto |
| 11 | Produtos | Desativar a Empada de Bacon | Cartão acinzentado com "INATIVO"; ela some do Novo Pedido |
| 12 | Produtos | Excluir a Empada de Frango | Bloqueado: já aparece em pedidos (sugere desativar) |
| 13 | Clientes | Novo cliente com telefone "123" | Aviso de telefone inválido |
| 14 | Clientes | Novo cliente com e-mail "ana@" | Aviso de e-mail inválido |
| 15 | Clientes | Buscar "9981" | Encontra a Ana Souza pelo telefone |
| 16 | Clientes | Excluir a Ana Souza | Bloqueado: possui pedidos |
| 17 | Novo Pedido | Clicar em "Revisar e Confirmar" sem cliente | Aviso "Selecione o cliente do pedido" |
| 18 | Novo Pedido | Selecionar o cliente e confirmar sem itens | Aviso "Adicione pelo menos um produto" |
| 19 | Novo Pedido | Clicar 3 vezes em + no Frango | Uma linha com quantidade 3, subtotal R$ 24,00 e "3 no pedido" no cartão |
| 20 | Novo Pedido | Usar − até zerar ou clicar em Remover | Item sai da comanda e o total é recalculado |
| 21 | Novo Pedido | Revisar e Confirmar → OK | Mensagem com o número do pedido e abertura dos Detalhes |
| 22 | Pedidos | Filtrar por status "Pendente" | Somente pendentes |
| 23 | Detalhes | Marcar como Entregue | Selo muda para ENTREGUE e os botões ficam desabilitados |
| 24 | Detalhes | Cancelar um pedido pendente | Selo CANCELADO; o valor sai do faturamento do Início |
