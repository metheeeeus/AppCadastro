/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.meuprojeto.appcadastro;

/**
 *
 * @author funcionaporfavor
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLRepository {
    // 1. Variável estática privada (A única instância do Singleton)
    private static MySQLRepository instance;
    private Connection connection;

    // 2. Construtor privado (Impede que façam 'new MySQLRepository()')
    private MySQLRepository() {
        conectarEInicializarBanco();
    }

    // 3. Método público estático (O ponto global de acesso)
    public static MySQLRepository getInstance() {
        if (instance == null) {
            instance = new MySQLRepository();
        }
        return instance;
    }

    private void conectarEInicializarBanco() {
        try {
            // Conecta primeiro sem banco de dados específico para criá-lo se não existir
            String urlRaiz = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false";
            Connection connTemp = DriverManager.getConnection(urlRaiz, "root", "minhasenhasecreta");
            Statement stmtTemp = connTemp.createStatement();
            stmtTemp.executeUpdate("CREATE DATABASE IF NOT EXISTS app_db");
            connTemp.close();

            // Agora conecta ao banco app_db especificamente
            String urlDB = "jdbc:mysql://localhost:3306/app_db?allowPublicKeyRetrieval=true&useSSL=false";
            this.connection = DriverManager.getConnection(urlDB, "root", "minhasenhasecreta");

            // Cria a tabela de usuários caso não exista
            String createTable = "CREATE TABLE IF NOT EXISTS usuarios ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY," 
                    + "nome VARCHAR(100) NOT NULL," 
                    + "email VARCHAR(100) UNIQUE NOT NULL," 
                    + "telefone VARCHAR(20)," 
                    + "senha VARCHAR(100) NOT NULL)";
            Statement stmt = connection.createStatement();
            stmt.execute(createTable);

        } catch (SQLException e) {
            System.err.println("Erro Crítico de Conexão: " + e.getMessage());
        }
    }

    // Método para Adicionar Usuário (CREATE)
    public void adicionarUsuario(Usuario u) {
        String sql = "INSERT INTO usuarios (nome, email, telefone, senha) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, u.getNome());
            pst.setString(2, u.getEmail());
            pst.setString(3, u.getTelefone());
            pst.setString(4, u.getSenha());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar: " + e.getMessage());
        }
    }

    // Método para Listar Usuários (READ)
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getInt("id"), rs.getString("nome"),
                    rs.getString("email"), rs.getString("telefone"), rs.getString("senha")
                );
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar: " + e.getMessage());
        }
        return lista;
    }
}