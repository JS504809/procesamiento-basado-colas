package com.ejemplo.queuepoc;
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;

/**
 * Hello world!
 *
 */

public class App {
    public static void main(String[] args) {
        // Parámetros de conexión
        String ACCOUNT_NAME = "pfazlabsa01";
        String queueName = "testqueue";
        // del 03 al 14 de noviembre, 2025.
        String SAS_TOKEN = "sv=2024-11-04&ss=q&srt=sco&sp=rwdlacup&se=2025-11-14T23:56:13Z&st=2025-11-03T15:41:13Z&spr=https&sig=EuA%2FwP4dXV7lKu2Y9Z58Ce%2FMuqOYigWro1nf0iAixIU%3D";
        
        // Crear el cliente de la cola
        String queueURL = String.format("https://%s.queue.core.windows.net/%s", ACCOUNT_NAME, queueName);
        QueueClient queueClient = new QueueClientBuilder()
            .endpoint(queueURL)
            .sasToken(SAS_TOKEN)
            .buildClient();
        queueClient.createIfNotExists();
        
        // Producción de mensaje(s)
        String[] messages = new String[] {"Mensaje 1", "Mensaje 2", "Mensaje 3", "Mensaje 4", "Mensaje 5"};
        for (String msg : messages) {
            queueClient.sendMessage(msg);
            System.out.println("Mensaje enviado: " + msg);
        }

        // Consumo de mensaje(s)
        QueueMessageItem message = queueClient.receiveMessages(1).stream().findFirst().orElse(null);
        if(message != null) {
            System.out.println("Mensaje recibido: " + message.getMessageText());

            // Eliminar el mensaje de la cola
            queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
            System.out.println("Mensaje eliminado");
        } else {
            System.out.println("No hay mensajes en la cola");
        }
        System.exit(0);
    }
}