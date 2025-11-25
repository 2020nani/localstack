# localstack

Observacao:
Rodar o comando docker-compose up -d na raiz, caso de erro sera necessario adicionar permissao para que o docker acessa a pasta no windowns
Abra Docker Desktop

Vá em Settings

Vá em Resources > File Sharing

Verifique se a pasta está listada:
Se NÃO estiver → clique em Add folder

Adicione a pasta e clique em Apply & Restart

criar fila

aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name send-message

Validar se criou a fila
aws --endpoint-url=http://localhost:4566 sqs list-queues

Criar bucket s3
aws --endpoint-url=http://localhost:4566 s3 mb s3://send-message-bucket
Validar se criou bucket
aws --endpoint-url=http://localhost:4566 s3 ls
Criar perfil localstack
aws configure --profile localstack
preencha os dados
AWS Access Key ID: test
AWS Secret Access Key: test
Default region: us-east-1

Lista s3 profile configurado
aws --profile localstack --endpoint-url=http://localhost:4566 s3 ls

Deploy da Lambda
Obs: Para deploy lambda sempre tem que estar arquivo .zip

aws --endpoint-url=http://localhost:4566 lambda create-function \
    --function-name sendMessageLambda \
    --zip-file fileb://lambda.zip \
    --handler com.exemplo.Handler \
    --runtime java17 \
    --role arn:aws:iam::000000000000:role/lambda-role

aws --endpoint-url=http://localhost:4566 lambda create-function \
  --function-name sendMessageLambda \
  --runtime java17 \
  --handler com.example.Handler \
  --zip-file fileb://target/lambda-sqs-1.0.0-shaded.jar \
  --role arn:aws:iam::000000000000:role/lambda-role \
  --environment "Variables={QUEUE_URL=http://localhost:4566/000000000000/send-message, AWS_ACCESS_KEY_ID=test, AWS_SECRET_ACCESS_KEY=test, AWS_REGION=us-east-1}"


Criar EventBridge para simular agendamentos
aws --endpoint-url=http://localhost:4566 events put-rule \
  --name regraDiariaSendMessage \
  --schedule-expression "rate(1 minute)"

Adicionar Lambda como destino eventBridge

aws --endpoint-url=http://localhost:4566 events put-targets \
  --rule regraDiariaSendMessage \
  --targets "Id"="1","Arn"="arn:aws:lambda:us-east-1:000000000000:function:sendMessageLambda"


enviar messagem dentro sqs
aws --endpoint-url=http://localhost:4566 lambda invoke \
  --function-name sendMessageLambda \
  response.json

