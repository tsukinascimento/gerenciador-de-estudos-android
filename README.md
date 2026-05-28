# Gerenciador de Estudos

Um aplicativo Android nativo, reativo e moderno, construído para ajudar estudantes a organizarem as suas matérias, tarefas e provas. O projeto foi desenvolvido com foco em **Clean Architecture**, **UX/UI avançada** e integração profunda com o sistema operacional Android.

## Funcionalidades Principais

* **Dashboard Reativo:** Visão geral estatística em tempo real do progresso nos estudos.
* **Gestão Completa (CRUD):** Criação, edição e exclusão de Matérias, Tarefas e Provas.
* **Persistência de Preferências:** Alternância entre Modo Claro/Escuro (`Dark/Light Mode`) com salvamento permanente no disco via `SharedPreferences`.
* **Notificações Nativas:** Alertas em segundo plano agendados no sistema do dispositivo.
* **Interações Premium:** Gestos nativos de `Swipe-to-Dismiss` (deslizar para apagar) com animações de cor e pop-ups de confirmação de segurança.

---

## Arquitetura e Padrões de Projeto

Este projeto foi construído seguindo rigorosamente a arquitetura **MVVM (Model-View-ViewModel)**, garantindo a separação de responsabilidades, testabilidade e um fluxo de dados unidirecional.

1. **Model (Data Layer):** 
   * Implementado utilizando a biblioteca **Room Database** para abstração do SQLite.
   * As entidades (`Task`, `Subject`, `Exam`) são acessadas através de `DAOs` (Data Access Objects).
   * O padrão **Repository** atua como uma única fonte da verdade (*Single Source of Truth*), centralizando a busca de dados.

2. **ViewModel:**
   * Atua como a ponte entre o repositório de dados e a interface gráfica.
   * Utiliza **Kotlin Coroutines** para executar consultas ao banco de dados em *Background Threads*, evitando o bloqueio da *Main Thread*.
   * Expõe o estado da aplicação utilizando **StateFlow**, permitindo que a interface reaja automaticamente a qualquer mudança no banco de dados.

3. **View (UI Layer):**
   * Desenvolvida de forma 100% declarativa utilizando **Jetpack Compose**.
   * O ecossistema reage aos fluxos do `StateFlow`, redesenhando apenas os componentes necessários (Recomposição Inteligente).

---

## Desafios Técnicos Superados

Desenvolver uma aplicação moderna para o ecossistema Android atual exige lidar com fragmentação de versões e novas políticas de segurança do Google. Aqui estão os principais desafios técnicos solucionados neste projeto:

### 1. Notificações e Permissões no Android 13+ (Tiramisu)
A partir da API 33, o Android removeu a permissão implícita de notificações.
* **A Solução:** Implementação de um `ActivityResultContracts.RequestPermission` diretamente na `MainActivity` para solicitar a permissão dinamicamente no primeiro acesso do usuário. 
* **O Agendamento:** Utilização do `AlarmManager` para registrar `PendingIntents` precisos, acionando um `BroadcastReceiver` (`ExamNotificationReceiver`) que roda em segundo plano, constrói o `NotificationChannel` (Obrigatório no Android 8+) e dispara a notificação visual na tela, mesmo com o app fechado.

### 2. Splash Screen Dinâmica (API 31+) e Compatibilidade
O Android 12 introduziu uma nova API nativa de Splash Screens que entrava em conflito com as cores de abertura em versões mais antigas.
* **A Solução:** Configuração de estilos seletivos no `themes.xml` (Day/Night) utilizando a tag `tools:targetApi="31"` no atributo `android:windowSplashScreenBackground`. Isso garantiu que aparelhos modernos tivessem a transição de cor suave baseada no tema do celular (Dark/Light), sem causar *crashes* em dispositivos mais antigos (API 24).

### 3. Adaptação Arquitetural do Jetpack Compose (Swipe-To-Dismiss)
Durante o desenvolvimento, a API de deslize do Material 3 sofreu uma atualização de arquitetura (o método `confirmValueChange` foi descontinuado pelo Google).
* **A Solução:** Refatoração da interação de Swipe migrando de um modelo de intercepção de estado para uma abordagem baseada em eventos reativos utilizando `LaunchedEffect`. O estado agora flui naturalmente e o cartão é "resetado" via código de forma invisível enquanto o *AlertDialog* de exclusão é exibido na tela.

---

## Tecnologias Utilizadas

* **Linguagem:** [Kotlin](https://kotlinlang.org/)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Arquitetura:** MVVM (Model-View-ViewModel)
* **Banco de Dados Local:** [Room](https://developer.android.com/training/data-storage/room)
* **Assincronismo & Fluxo de Dados:** Coroutines & StateFlow
* **Navegação:** Jetpack Navigation Compose
* **Agendamento em Background:** AlarmManager & BroadcastReceivers

---
