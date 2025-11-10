# PoC: Azure Service Bus (Java x Maven)

- Este proyecto es una prueba de concepto (PoC) para demostrar cómo utilizar **Azure Service Bus** (Queue) desde una aplicación Java ejecutándose localmente. 
- El proyecto permite crear una cola, enviar un mensaje y recibirlo usando la SDK oficial de Azure.

---

## Requisitos previos

- **Java 8 o superior**
- **Maven**
- **Service Bus Namespace** con permisos para crear recursos
- **Acceso a Internet**

---

## Aprovisionamiento en Azure

1. Ingresa al [Portal de Azure](https://portal.azure.com).
2. Crea un **Service Bus Namespace**:
    - Ve a *Service Bus* → *Create*.
    - Elige el grupo de recursos, nombre, región, Pricing Tier (*Basic*, *Standard* o *Premium*).
    - Finaliza la creación.
3. Dentro del namespace, crea una **Queue**, por ejemplo `demoqueue`.
4. Ve a *Shared Access Policies*, selecciona `RootManageSharedAccessKey` y copia la **Connection String–Primary Key**.

---

## Estructura del Proyecto

```
shbuspoc/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── ejemplo/
                    └── shbuspoc/
                        └── App.java
```

---
## Configuración

1. Clona o descarga este repositorio.
2. Abre el archivo `App.java` y reemplaza la variable `connectStr` con tu cadena de conexión de Azure Storage.


## Instalación de dependencias

Para instalar las dependencias y compilar el proyecto:

```bash
mvn compile
```

---

## Ejecución

Para ejecutar la prueba completa:

```bash
mvn exec:java -Dexec.mainClass="com.ejemplo.shbuspoc.App"
```

---

## Ejemplo de Código Básico

```java
import com.azure.messaging.servicebus.*;
import io.github.cdimascio.dotenv.Dotenv;

public class App {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String connectionString = dotenv.get("AZURE_SERVICEBUS_CONNECTION_STRING");
        String queueName = dotenv.get("AZURE_SERVICEBUS_QUEUE_NAME");

        // Enviar
        try (ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient()) {
            senderClient.sendMessage(new ServiceBusMessage("¡Hola desde Service Bus en Java!"));
            System.out.println("Mensaje enviado exitosamente a Service Bus.");
        }

        // Recibir
        try (ServiceBusReceiverClient receiverClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .buildClient()) {
            ServiceBusReceivedMessage message = receiverClient.receiveMessages(1).stream().findFirst().orElse(null);

            if (message != null) {
                System.out.println("Mensaje recibido: " + message.getBody().toString());
                receiverClient.complete(message);
                System.out.println("Mensaje procesado y eliminado de la cola.");
            } else {
                System.out.println("No hay mensajes en la cola.");
            }
        }

        System.exit(0);
    }
}
```

---

## Notas

- **Seguridad:** No compartas tu cadena de conexión ni claves privadas de Azure.
- **Roles y políticas:** Para producción, usa políticas con menos privilegios.
- **Documentación:**  
    - [SDK Java para Service Bus](https://learn.microsoft.com/en-us/java/api/overview/azure/servicebus-readme?view=azure-java-stable)
    - [Referencia Azure Service Bus SDK Java](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/servicebus/azure-messaging-servicebus)

---

## Licencia

Este proyecto es únicamente para fines didácticos y demostrativos.