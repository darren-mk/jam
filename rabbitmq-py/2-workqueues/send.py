import pika

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()

# create a hello queue to which the message will be delivered
channel.queue_declare(queue='hello')

# send message to hello queue
channel.basic_publish(exchange='',
                      routing_key='hello',
                      body='hello, darren')
