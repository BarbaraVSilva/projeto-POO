package com.retailhub.model;

public class Usuario {
    private int id;
    private String login;
    private String senha;
    private String perfil;
    private int statusAtivo;
    private String dataUltimaSenha;

    public Usuario() {}

    public Usuario(int id, String login, String senha, String perfil, int statusAtivo, String dataUltimaSenha) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
        this.statusAtivo = statusAtivo;
        this.dataUltimaSenha = dataUltimaSenha;
    }

    public Usuario(int id, String login, String senha, String perfil, int statusAtivo) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
        this.statusAtivo = statusAtivo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    
    public int getStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(int statusAtivo) { this.statusAtivo = statusAtivo; }
    
    public String getDataUltimaSenha() { return dataUltimaSenha; }
    public void setDataUltimaSenha(String dataUltimaSenha) { this.dataUltimaSenha = dataUltimaSenha; }
    
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(perfil);
    }
}
