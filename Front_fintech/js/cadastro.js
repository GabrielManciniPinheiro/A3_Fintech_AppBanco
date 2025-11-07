// --- Lógica para cadastro.html ---

const form = document.getElementById('cadastroForm');
const btnCriarConta = document.getElementById('btnCriarConta');
const mensagemErro = document.getElementById('mensagemErro');

// --- FUNÇÃO DE VALIDAÇÃO DE CPF ADICIONADA ---
function isCpfValido(cpf) {
    cpf = cpf.replace(/[^\d]+/g, '');
    if (cpf.length !== 11) return false;

    // Elimina CPFs inválidos conhecidos (todos os dígitos iguais)
    if (/^(\d)\1{10}$/.test(cpf)) return false;

    let soma = 0;
    let resto;

    // --- Valida 1º dígito ---
    for (let i = 1; i <= 9; i++) {
        soma = soma + parseInt(cpf.substring(i - 1, i)) * (11 - i);
    }
    resto = (soma * 10) % 11;
    if ((resto == 10) || (resto == 11)) resto = 0;
    if (resto != parseInt(cpf.substring(9, 10))) return false;

    soma = 0;
    // --- Valida 2º dígito ---
    for (let i = 1; i <= 10; i++) {
        soma = soma + parseInt(cpf.substring(i - 1, i)) * (12 - i);
    }
    resto = (soma * 10) % 11;
    if ((resto == 10) || (resto == 11)) resto = 0;
    if (resto != parseInt(cpf.substring(10, 11))) return false;

    return true; // CPF é válido
}
// --- FIM DA FUNÇÃO ---


form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const nome = document.getElementById('nome').value;
    const cpf = document.getElementById('cpf').value;
    const email = document.getElementById('email').value;
    const telefone = document.getElementById('telefone').value;
    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;

    mensagemErro.textContent = ''; // Limpa erros antigos

    // --- VALIDAÇÕES DO FRONTEND ---
    if (senha !== confirmarSenha) {
        mensagemErro.textContent = 'As senhas não coincidem!';
        return;
    }
    if (senha.length < 6) {
        mensagemErro.textContent = 'Senha deve ter no mínimo 6 caracteres.';
        return;
    }

    // --- NOVA VALIDAÇÃO DE CPF ADICIONADA ---
    if (!isCpfValido(cpf)) {
        mensagemErro.textContent = 'CPF inválido. Verifique os dígitos.';
        return;
    }
    // --- FIM DA NOVA VALIDAÇÃO ---


    const dadosCadastro = { nome, cpf, email, telefone, senha, confirmarSenha };
    btnCriarConta.disabled = true;
    btnCriarConta.textContent = 'Criando...';

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
            // Pega o erro do Backend (que agora também valida o @CPF)
            mensagemErro.textContent = erro.erros ? erro.erros[0] : (erro.mensagem || 'Erro ao cadastrar.');
        }

    } catch (error) {
        console.error('Erro de rede:', error);
        mensagemErro.textContent = 'Não foi possível conectar ao servidor. Tente mais tarde.';
    } finally {
        btnCriarConta.disabled = false;
        btnCriarConta.textContent = 'Criar conta';
    }
});