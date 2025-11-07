// --- Lógica para login.html ---

const loginForm = document.getElementById('loginForm');
const btnEntrar = document.getElementById('btnEntrar');
const mensagemErro = document.getElementById('mensagemErro');
const togglePassword = document.getElementById('togglePassword');
const senhaInput = document.getElementById('senha');

// Funcionalidade 1: Mostrar/Ocultar Senha
togglePassword.addEventListener('click', (e) => {
    e.preventDefault(); // Impede o submit do formulário
    const type = senhaInput.getAttribute('type') === 'password' ? 'text' : 'password';
    senhaInput.setAttribute('type', type);
});

// Funcionalidade 2: Login
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const cpf = document.getElementById('cpf').value;
    const senha = document.getElementById('senha').value;

    if (cpf.length !== 11) {
        mensagemErro.textContent = 'CPF deve ter 11 dígitos.';
        return;
    }

    const dadosLogin = { cpf, senha };
    btnEntrar.disabled = true;
    btnEntrar.textContent = 'Entrando...';
    mensagemErro.textContent = '';

    try {
        const response = await fetch('http://localhost:8080/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosLogin)
        });

        if (response.ok) { // Status 200-299
            const data = await response.json();

            // Salva os dados no "cofre" do navegador
            localStorage.setItem('jwtToken', data.token);
            localStorage.setItem('userName', data.nomeUsuario);

            window.location.href = 'home.html';
        } else {
            const erro = await response.json();
            mensagemErro.textContent = erro.mensagem || 'Usuário ou senha inválidos.';
        }

    } catch (error) {
        console.error('Erro de rede:', error);
        mensagemErro.textContent = 'Não foi possível conectar ao servidor. Tente mais tarde.';
    } finally {
        btnEntrar.disabled = false;
        btnEntrar.textContent = 'Entrar';
    }
});