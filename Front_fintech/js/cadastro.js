// --- Lógica para cadastro.html ---

const form = document.getElementById('cadastroForm');
const btnCriarConta = document.getElementById('btnCriarConta');
const mensagemErro = document.getElementById('mensagemErro');

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const nome = document.getElementById('nome').value;
    const cpf = document.getElementById('cpf').value;
    const email = document.getElementById('email').value;
    const telefone = document.getElementById('telefone').value;
    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;

    if (senha !== confirmarSenha) {
        mensagemErro.textContent = 'As senhas não coincidem!';
        return;
    }
    if (cpf.length !== 11) {
        mensagemErro.textContent = 'CPF deve ter 11 dígitos.';
        return;
    }
     if (senha.length < 6) {
        mensagemErro.textContent = 'Senha deve ter no mínimo 6 caracteres.';
        return;
    }

    const dadosCadastro = { nome, cpf, email, telefone, senha, confirmarSenha };
    btnCriarConta.disabled = true;
    btnCriarConta.textContent = 'Criando...';
    mensagemErro.textContent = '';

    try {
        const response = await fetch('http://localhost:8080/api/v1/usuarios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosCadastro)
        });

        if (response.status === 201) {
            alert('Conta criada com sucesso! Você será redirecionado para o login.');
            window.location.href = 'login.html';
        } else {
            const erro = await response.json();
            mensagemErro.textContent = erro.mensagem || 'Erro ao cadastrar. Tente novamente.';
        }

    } catch (error) {
        console.error('Erro de rede:', error);
        mensagemErro.textContent = 'Não foi possível conectar ao servidor. Tente mais tarde.';
    } finally {
        btnCriarConta.disabled = false;
        btnCriarConta.textContent = 'Criar conta';
    }
});