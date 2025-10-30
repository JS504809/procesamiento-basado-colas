# PoC: Azure Queue Storage (Java x Maven)
- Este proyecto es una prueba de concepto (PoC) para demostrar el uso del servicio de mensajería de Azure **Queue Storage** desde una aplicación Java. 
- El proyecto permite crear una cola, producir un mensaje y consumirlo usando la SDK oficial de Azure.

## Requisitos
- **Java 8 o superior**
- **Maven**
- **Storage Account** con permisos para crear recursos
- **Acceso a Internet**

## Aprovisionamiento en Azure

1. Inicia sesión en el [Portal de Azure](https://portal.azure.com).
2. Crea una **Storage Account** ("Cuenta de almacenamiento"):
   - Ve a *Storage accounts* → *Create*.
   - Selecciona el grupo de recursos, nombre, región, *Standard* y *Locally Redundant Storage* (LRS).
   - Finaliza la creación.
3. Dentro de la Storage Account, ve a *Queues* y crea una nueva cola, por ejemplo: `testqueue`.
4. Obtén la **connection string** en el menú izquierdo, sección *Access keys*.

## Estructura del Proyecto

```
queuepoc/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── ejemplo/
                    └── queuepoc/
                        └── App.java
```

## Configuración

1. Clona o descarga este repositorio.
2. Abre el archivo `App.java` y reemplaza la variable `connectStr` con tu cadena de conexión de Azure Storage.

## Instalación de Dependencias

El proyecto usa Maven. Para instalar dependencias y compilar el proyecto:

```bash
mvn compile
```

## Ejecución

Para ejecutar el proyecto y probar la conexión con Azure Queue Storage:

```bash
mvn exec:java -Dexec.mainClass="com.ejemplo.queuepoc.App"
```

## Ejemplo de Código Básico

```java
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;

public class App {
    public static void main(String[] args) {
        String connectStr = "<TU_CONNECTION_STRING>";
        String queueName = "testqueue";

        QueueClient queueClient = new QueueClientBuilder()
            .connectionString(connectStr)
            .queueName(queueName)
            .buildClient();

        queueClient.createIfNotExists();
        queueClient.sendMessage("¡Hola desde Java!");

        QueueMessageItem message = queueClient.receiveMessages(1).stream().findFirst().orElse(null);
        if (message != null) {
            System.out.println("Mensaje recibido: " + message.getMessageText());
            queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
        }
    }
}
```

## Notas

- **Seguridad**: No compartas tu cadena de conexión ni claves de Azure.
- **Firewall**: Por defecto, el Storage Account acepta conexiones públicas. Puedes restringir esto desde la configuración de red.
- **Documentación oficial**:  
  - [Azure Storage Queues Java Docs](https://learn.microsoft.com/en-us/java/api/overview/azure/storage-queue-readme?view=azure-java-stable)
  - [SDK Azure Storage Queue en GitHub](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/storage/azure-storage-queue)

## Licencia

Este proyecto es solo para fines de demostración y aprendizaje.