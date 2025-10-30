package com.ejemplo.queuepoc;
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;

/**
 * Hello world!
 *
 */
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        // Reemplaza esto por tu cadena de conexión
        String connectStr = "DefaultEndpointsProtocol=https;AccountName=TU_ACCOUNT_NAME;AccountKey=TU_ACCOUNT_KEY;EndpointSuffix=core.windows.net";
        String queueName = "testqueue";

        // Crear el cliente de la cola
        QueueClient queueClient = new QueueClientBuilder()
            .connectionString(connectStr)
            .queueName(queueName)
            .buildClient();

        // Crear la cola si no existe
        queueClient.createIfNotExists();

        // Producir un mensaje
        queueClient.sendMessage("¡Hola desde Java!");

        // Consumir un mensaje
        QueueMessageItem message = queueClient.receiveMessages(1).stream().findFirst().orElse(null);
        if(message != null) {
            logger.info("Mensaje recibido: " + message.getMessageText());

            // Eliminar el mensaje de la cola
            queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
            logger.info("Mensaje eliminado");
        } else {
            logger.info("No hay mensajes en la cola");
        }
    }
}