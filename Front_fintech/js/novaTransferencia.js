// --- Lógica para novaTransferencia.html ---

const cpfInput = document.getElementById('cpfDestinatario');
const btnContinuar = document.getElementById('btnContinuar');
const form = document.getElementById('cpfForm');
const mensagemErro = document.getElementById('mensagemErro');

// Funcionalidade 1: Habilitar botão (vermelho)
cpfInput.addEventListener('input', () => {
    if (cpfInput.value.length === 11) {
        btnContinuar.disabled = false;
    } else {
        btnContinuar.disabled = true;
    }
});

// Funcionalidade 2: Buscar destinatário
form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const cpfDestinatario = cpfInput.value;
    btnContinuar.disabled = true;
    btnContinuar.textContent = 'Verificando...';
    mensagemErro.textContent = '';

    try {
        const response = await fetch(`http://localhost:8080/api/v1/usuarios/cpf/${cpfDestinatario}`, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const data = await response.json();

            localStorage.setItem('destCpf', data.cpf);
            localStorage.setItem('destName', data.nome);

            window.location.href = 'transferencia-valor.html';

        } else {
            const erro = await response.json();
            mensagemErro.textContent = erro.mensagem || 'CPF não encontrado.';
        }

    } catch (error) {
        console.error('Erro de rede:', error);
        mensagemErro.textContent = 'Erro ao conectar ao servidor.';
    } finally {
        btnContinuar.disabled = false;
        btnContinuar.textContent = 'Continuar';
    }
});