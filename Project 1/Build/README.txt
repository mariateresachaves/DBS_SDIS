Building
#Nota necessário ANT 1.8 ou superior
#Separamos as pastas visto que esta foi desenhada para compilar segundo uma forma que pode nao ser global para todos os elementos do grupo

#Compilar o programa
#O comando seguinte irá procurar na directoria um ficheiro de compilação XML que seguirá para compilar a solucção
ant -f <PathTo>/Servico -Dnb.internal.action.name=rebuild clean jar

#O Script gerará um jar com o código do programa indicando onde o guardou (Geralmente numa pasta dist [distribuition] sob o directorio Serviço)
#Deverá ainda ser colocado, no caminho de onde será executado o ficheiro, um ficheiro de propriedades que terá todas as informações para a aplicação correr
#O ficheiro de propriedades estará no mesmo directorio que o caminho <PathTo>/Servico

(Opcionalmente)-> cd "<pathTo>/bin/"
(Opcionalmente)-> cd rmiregistry &
(Opcionalmente)-> cd -
#O ultimo comando muda o directorio para o previamente definido
#Correr
java -jar "<pathTo>/Servico/dist/Servico.jar" settings.properties


#Compilar a testing APP
#O comando seguinte irá procurar na directoria um ficheiro de compilação XML que seguirá para compilar a solucção
ant -f <PathTo>/TestingApp -Dnb.internal.action.name=rebuild clean jar

#O Script gerará um jar com o código do programa indicando onde o guardou (Geralmente numa pasta dist [distribuition] sob o directorio Serviço)
#Deverá ainda ser colocado, no caminho de onde será executado o ficheiro, um ficheiro de propriedades que terá todas as informações para a aplicação correr
#O ficheiro de propriedades estará no mesmo directorio que o caminho <PathTo>/Servico
#Correr

java -jar "<pathTo>/TestingApp/dist/Servico.jar" <SenderID> <command>

<SenderID> -> Valor definido no serviço onde o identifica
<command> -> Comando como descrito na indicação para iniciar o protocolo ie: backup <file> <RD>

