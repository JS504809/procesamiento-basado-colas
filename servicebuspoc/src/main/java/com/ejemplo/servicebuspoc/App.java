package com.ejemplo.servicebuspoc;
import com.azure.messaging.servicebus.*;
import com.azure.core.credential.AzureSasCredential;
import com.azure.identity.*;
import io.github.cdimascio.dotenv.Dotenv;

public class App {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String fullyQualifiedNamespace = dotenv.get("FULLY_QUALIFIED_NAMESPACE");
        String sasToken = dotenv.get("SAS_TOKEN");

        /*
        * Operaciones con Azure Service Bus - Colas
        */
        String queueName = dotenv.get("QUEUE_NAME");
        // ----- Envío de mensajes a una queue -----
        try (ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .credential(fullyQualifiedNamespace, 
                    new DefaultAzureCredentialBuilder().build() // Usar este método si se conecta con Managed Identity
                    // new AzureSasCredential(sasToken) // Usar este método si se conecta con SAS Token
                )
                .sender()
                .queueName(queueName)
                .buildClient()) {

            senderClient.sendMessage(new ServiceBusMessage("¡Hola desde Service Bus en Java!"));
            System.out.println("[Queues] Mensaje enviado exitosamente a Service Bus.");
        }

        // Recibir un mensaje
        try (ServiceBusReceiverClient receiverClient = new ServiceBusClientBuilder()
                .credential(fullyQualifiedNamespace, 
                    new DefaultAzureCredentialBuilder().build() // Usar este método si se conecta con Managed Identity
                    // new AzureSasCredential(sasToken) // Usar este método si se conecta con SAS Token
                )
                .receiver()
                .queueName(queueName)
                .buildClient()) {
            ServiceBusReceivedMessage message = receiverClient.receiveMessages(1).stream().findFirst().orElse(null);

            if (message != null) {
                System.out.println("[Queues] Mensaje recibido: " + message.getBody().toString());
                receiverClient.complete(message);
                System.out.println("[Queues] Mensaje procesado y eliminado de la cola.");
            } else {
                System.out.println("[Queues] No hay mensajes en la cola.");
            }
        }

        /*
        * Operaciones con Azure Service Bus - Temas y Suscripciones
        */
        String topicName = dotenv.get("TOPIC_NAME");
        String subscriptionName = dotenv.get("SUBSCRIPTION_NAME");
        // ----- Envío de mensajes a un topic con propiedades -----
        try (ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .credential(fullyQualifiedNamespace, 
                    new DefaultAzureCredentialBuilder().build() // Usar este método si se conecta con Managed Identity
                    // new AzureSasCredential(sasToken) // Usar este método si se conecta con SAS Token
                )
                .sender()
                .topicName(topicName)
                .buildClient()) {

            // Mensaje que será filtrado según properties
            ServiceBusMessage messagePrioridadAlta = new ServiceBusMessage("Prioridad alta");
            messagePrioridadAlta.getApplicationProperties().put("prioridad", "alta");
            
            ServiceBusMessage messagePrioridadMedia = new ServiceBusMessage("Prioridad media");
            messagePrioridadMedia.getApplicationProperties().put("prioridad", "media");

            ServiceBusMessage messagePrioridadBaja = new ServiceBusMessage("Prioridad baja");
            messagePrioridadBaja.getApplicationProperties().put("prioridad", "baja");
            
            senderClient.sendMessage(messagePrioridadAlta);
            senderClient.sendMessage(messagePrioridadMedia);
            senderClient.sendMessage(messagePrioridadBaja);

            System.out.println("[Pub/Sub] Enviados mensajes con propiedades prioridad=alta, prioridad=media y prioridad=baja.");
        }

        // ----- Consumo de mensajes desde una suscripción (filtrada en portal) -----
        try (ServiceBusReceiverClient receiverClient = new ServiceBusClientBuilder()
                .credential(fullyQualifiedNamespace, 
                    new DefaultAzureCredentialBuilder().build() // Usar este método si se conecta con Managed Identity
                    // new AzureSasCredential(sasToken) // Usar este método si se conecta con SAS Token
                )
                .receiver()
                .topicName(topicName)
                .subscriptionName(subscriptionName)
                .buildClient()) {

            System.out.println("[Pub/Sub] Recibiendo mensajes para la suscripción: " + subscriptionName);
            for (ServiceBusReceivedMessage msg : receiverClient.receiveMessages(10)) {
                System.out.println("[Pub/Sub] Mensaje: " + msg.getBody().toString());
                System.out.println("[Pub/Sub] Propiedades: " + msg.getApplicationProperties());
                receiverClient.complete(msg);
            }
        }
        System.exit(0);
    }
}