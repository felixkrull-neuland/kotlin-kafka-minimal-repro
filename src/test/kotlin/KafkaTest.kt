import io.github.nomisRev.kafka.receiver.KafkaReceiver
import io.github.nomisRev.kafka.receiver.ReceiverSettings
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class KafkaTest {
    private val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.3"))

    @BeforeTest
    fun setup() {
        kafka.start()
    }

    @Test
    fun testReceiver() {
        val producer = KafkaProducer(
            mapOf("bootstrap.servers" to kafka.bootstrapServers),
            StringSerializer(),
            StringSerializer()
        )
        val receiverSettings = ReceiverSettings(
            bootstrapServers = kafka.bootstrapServers,
            keyDeserializer = StringDeserializer(),
            valueDeserializer = StringDeserializer(),
            groupId = "test"
        )
        val receiver = KafkaReceiver(receiverSettings)

        producer.send(ProducerRecord("topic", "key", "value")).get()
        val record = runBlocking {
            withTimeout(5.seconds) {
                receiver.receive("topic").take(1).single()
            }
        }

        assertEquals("key", record.key())
        assertEquals("value", record.value())
    }
}
