// --- Lógica para transferencia-valor.html ---

const form = document.getElementById('valorForm');
const btnConfirmar = document.getElementById('btnConfirmar');
const mensagemEl = document.getElementById('mensagem');
const valorInput = document.getElementById('valor');
const linkVoltar = document.getElementById('link-voltar');

const token = localStorage.getItem('jwtToken');
const userCpf = localStorage.getItem('userCpf'); // CPF do Remetente (logado)
const destCpf = localStorage.getItem('destCpf');
const destName = localStorage.getItem('destName');

// Funcionalidade 1: Ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    if (!token || !userCpf || !destCpf || !destName) {
        alert('Ocorreu um erro. Tente o fluxo de transferência novamente.');
        localStorage.removeItem('destCpf');
        localStorage.removeItem('destName');
        window.location.href = 'home.html';
        return;
    }

    document.getElementById('dest-nome').textContent = destName;
    document.getElementById('dest-cpf').textContent = `CPF: ${destCpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.***.***-$4")}`;
});

// Funcionalidade 2: Realizar a Transferência
form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const valor = parseFloat(valorInput.value);
    if (valor <= 0) {
        mensagemEl.className = 'msg-erro';
        mensagemEl.textContent = 'O valor deve ser positivo.';
        return;
    }

    const dadosTransferencia = {
        cpfRemetente: userCpf,
        cpfDestinatario: destCpf,
        valor: valor
    };

    btnConfirmar.disabled = true;
    btnConfirmar.textContent = 'Processando...';
    mensagemEl.textContent = '';

    try {
        const response = await fetch('http://localhost:8080/api/v1/transferencias/pix', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosTransferencia)
        });

        const data = await response.json();

        if (response.ok) { // SUCESSO! (Status 200)
            mensagemEl.className = 'msg-sucesso';
            mensagemEl.textContent = `Transferência de R$ ${valor.toFixed(2)} para ${destName} realizada com sucesso!`;
            form.style.display = 'none'; // Esconde o formulário
            linkVoltar.style.display = 'block'; // Mostra o link de voltar
        } else { // ERRO! (Status 400, 403, 404)
            mensagemEl.className = 'msg-erro';
            mensagemEl.textContent = data.mensagem; // Ex: "Saldo insuficiente." ou "Transação bloqueada por suspeita de fraude..."
        }

    } catch (error) {
        console.error('Erro de rede:', error);
        mensagemEl.className = 'msg-erro';
        mensagemEl.textContent = 'Erro ao conectar ao servidor.';
    } finally {
        if(mensagemEl.className === 'msg-sucesso') {
           // Não re-habilita o botão se deu sucesso
        } else {
           btnConfirmar.disabled = false;
           btnConfirmar.textContent = 'Confirmar Transferência';
        }
    }
});