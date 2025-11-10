package com.ejemplo.queuepoc;
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;
import com.azure.identity.DefaultAzureCredentialBuilder;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Hello world!
 *
 */

public class App {
    public static void main(String[] args) {
        // Parámetros de conexión
        Dotenv dotenv = Dotenv.load();
        String ACCOUNT_NAME = dotenv.get("ACCOUNT_NAME");
        String QUEUE_NAME = dotenv.get("QUEUE_NAME");
        String queueURL = String.format("https://%s.queue.core.windows.net/%s", ACCOUNT_NAME, QUEUE_NAME);
        String SAS_TOKEN = dotenv.get("SAS_TOKEN");
        
        // Configuración de la conexión a Azure Queue Storage
        QueueClient queueClient = new QueueClientBuilder()
            .endpoint(queueURL)
            // .sasToken(SAS_TOKEN) // Usar este método si se conecta con SAS Token
            .credential(new DefaultAzureCredentialBuilder().build()) // Usar este método si se conecta con Managed Identity
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