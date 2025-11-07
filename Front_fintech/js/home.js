// --- Lógica para home.html ---

// Variáveis globais para guardar os dados
let userBalance = 0;
let saldoVisivel = false;

// Mapeia os elementos do SEU HTML que acabamos de adicionar IDs
const saldoValorEl = document.getElementById('saldo-valor');
const toggleSaldoEl = document.getElementById('toggleSaldo'); // O SVG do "olho"
const userNameEl = document.getElementById('user-name-display'); // O <h1>
const avatarEl = document.getElementById('avatar-inicial'); // O <span>
const btnSignOut = document.getElementById('btnSignOut'); // --- BOTÃO DE SAIR ADICIONADO ---

// --- (Opcional) SVG do olho "aberto" (sem o traço) ---
const olhoAbertoSVG = `
    <path d="M3 10C3 10 7 4 12 4C17 4 21 10 21 10" stroke="#809BFF" stroke-width="2"/>
    <path d="M3 10C3 10 7 16 12 16C17 16 21 10 21 10" stroke="#809BFF" stroke-width="2"/>
    <circle cx="12" cy="10" r="2" fill="#809BFF"/>
`;
// --- (Opcional) SVG do olho "fechado" (com o traço) ---
const olhoFechadoSVG = `
    <path d="M3 10C3 10 7 4 12 4C17 4 21 10 21 10" stroke="#809BFF" stroke-width="2"/>
    <path d="M3 10C3 10 7 16 12 16C17 16 21 10 21 10" stroke="#809BFF" stroke-width="2"/>
    <circle cx="12" cy="10" r="2" fill="#809BFF"/>
    <line x1="4" y1="4" x2="20" y2="20" stroke="#809BFF" stroke-width="2"/>
`;

// --- Funcionalidade 1: Ao carregar a página ---
document.addEventListener('DOMContentLoaded', async () => {
    // 1. Verifica se o usuário está logado (procura o token)
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        // Se não tem token, expulsa para a tela de login
        alert('Você não está logado.');
        window.location.href = 'login.html';
        return;
    }

    // 2. Coloca o nome do usuário (que salvamos no login)
    const userName = localStorage.getItem('userName');
    if (userName) {
        userNameEl.textContent = userName;
        avatarEl.textContent = userName.charAt(0).toUpperCase();
    }

    // 3. Busca os dados completos da API (saldo, etc.)
    try {
        const response = await fetch('http://localhost:8080/api/v1/usuarios/me', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Envia o "passe" de segurança
            }
        });

        if (response.ok) {
            const data = await response.json();
            // Guarda o saldo na variável global
            userBalance = data.saldo;
            // Salva o CPF do usuário logado para usar na tela de transferência
            localStorage.setItem('userCpf', data.cpf);
        } else {
            // Se o token estiver errado ou expirado
            alert('Sua sessão expirou. Faça login novamente.');
            localStorage.clear(); // Limpa o "cofre" do navegador
            window.location.href = 'login.html';
        }
    } catch (error) {
        console.error('Erro de rede:', error);
        saldoValorEl.textContent = 'Erro de rede';
    }
});

// --- Funcionalidade 2: Mostrar/Ocultar Saldo (clique no "olho") ---
toggleSaldoEl.addEventListener('click', () => {
    saldoVisivel = !saldoVisivel; // Inverte o estado (true/false)

    if (saldoVisivel) {
        // Se for para mostrar, formata o número como moeda
        saldoValorEl.textContent = userBalance.toLocaleString('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        });
        // (Opcional) Mudar o ícone do "olho" para "olho aberto"
        toggleSaldoEl.innerHTML = olhoAbertoSVG;
    } else {
        // Se for para esconder, volta os "••••"
        saldoValorEl.textContent = '••••';
        // (Opcional) Mudar o ícone de volta para "olho fechado"
        toggleSaldoEl.innerHTML = olhoFechadoSVG;
    }
});

// --- FUNCIONALIDADE NOVA: Sair (Sign Out) ---
btnSignOut.addEventListener('click', () => {
    // 1. Limpa o "cofre" do navegador
    localStorage.clear();

    // 2. Redireciona para o login
    window.location.href = 'login.html';
});