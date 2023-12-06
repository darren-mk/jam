import pika

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()

channel.queue_declare(queue='hello')

def callback(ch, method, properties, body):
    print(f" [x] Received {body}")

channel.basic_consume(queue='hello',
                      auto_ack=True,
                      on_message_callback=callback)

channel.start_consuming()
# Received b'hello, darren'
