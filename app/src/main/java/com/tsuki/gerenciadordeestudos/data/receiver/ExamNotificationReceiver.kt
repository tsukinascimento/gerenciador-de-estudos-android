package com.tsuki.gerenciadordeestudos.data.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ExamNotificationReceiver : BroadcastReceiver() {

    // Esta função é executada automaticamente pelo Android quando o Alarme dispara
    override fun onReceive(context: Context, intent: Intent) {

        // 1. Recebemos o nome da prova que enviámos na hora de agendar
        val examTitle = intent.getStringExtra("EXAM_TITLE") ?: "Prova Importante!"

        // 2. Chamamos o "Gerente de Notificações" do telemóvel
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "exam_channel_id"

        // 3. A partir do Android 8 (Oreo), é OBRIGATÓRIO criar um "Canal" de notificações
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lembretes de Provas", // Nome que aparece nas configurações do telemóvel
                NotificationManager.IMPORTANCE_HIGH // Faz o telemóvel vibrar e tocar som
            ).apply {
                description = "Canal utilizado para avisar sobre a aproximação de provas"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 4. Desenhamos o visual da nossa notificação
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ícone padrão do sistema
            .setContentTitle("Lembrete de Prova \uD83D\uDCDA")
            .setContentText("A sua prova de '$examTitle' aproxima-se! Hora de rever a matéria.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Para aparecer no topo da tela
            .setAutoCancel(true) // Desaparece quando o utilizador clica nela
            .build()

        // 5. Disparamos a notificação! (Usamos o tempo atual como ID para não sobrepor outras)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}