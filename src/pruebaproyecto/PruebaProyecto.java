package pruebaproyecto;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class PruebaProyecto {

    public static void main(String[] args) throws IOException, InterruptedException {
        
        System.out.println("Eliminando residuos");
        
        generarNumeros();
        
        eliminarResiduos();
        
        TimeUnit.MILLISECONDS.sleep(2000);
        
        /*
        Este bloque lee todos los archivos que estan alrededor del ejecutable, y de ellos
        crea una lista de los archivos que sean txt
        */
        
        File folder = new File("./");
        File[] listaDeArchivos = folder.listFiles();
        
        ArrayList<String> archivosDisponibles = new ArrayList<>();
        
        System.out.println("Lista de archivos de texto: ");
        
        for (int i = 0; i < listaDeArchivos.length; i++) {
            if (listaDeArchivos[i].isFile() && listaDeArchivos[i].getName().toLowerCase().contains(".txt")) {
                archivosDisponibles.add(listaDeArchivos[i].getName());
            }
        }
        
        Scanner entradas = new Scanner(System.in);
        int opcionArchivo, opcionMetodo, opcionOrden;
        int numeroDatos = 0;
        
        System.out.println("Bienvenidos");
        System.out.println("Elige un archivo: ");
        
        for (int i = 0; i < archivosDisponibles.size(); ++i)
            System.out.print("\n[" + (i + 1) + "] - " + archivosDisponibles.get(i));
        System.out.print("\nOpcion: ");
        
        opcionArchivo = entradas.nextInt();
        
        Path miRuta = Paths.get(archivosDisponibles.get(opcionArchivo - 1));
        Scanner leerArchivoOriginal = new Scanner(miRuta);
        FileWriter copiarArchivoOriginal = new FileWriter("F0.txt");
        
        /*
        Copia el contenido del archivo y lo copia a F0.txt
        */
        
        while(leerArchivoOriginal.hasNextLine())
        {
            copiarArchivoOriginal.write(leerArchivoOriginal.nextLine());   //En teoria solo deberia copiar una linea
        }

        copiarArchivoOriginal.flush();
        copiarArchivoOriginal.close();
        leerArchivoOriginal.close();
        
        /*
        Este apartado cuenta los numeros del archivo, que seran utilizados
        mas adelante para manejar cuantos numeros hay por bloque
        */
        
        Scanner contarNumeros = new Scanner(miRuta);
        contarNumeros.useDelimiter(",");
        while(contarNumeros.hasNextFloat())
        {
            contarNumeros.nextFloat();
            ++numeroDatos;
        }
        
        contarNumeros.close();
        
        System.out.println("");
        System.out.println("Numero de datos detectados: ");
        System.out.print(numeroDatos);
        
        if (numeroDatos == 0)
        {
            System.out.println("\nNo hay suficientes datos");
            return;
        }
        
        System.out.println("\n");
        System.out.println("Por que metodo lo quieres?");
        System.out.println("1) Polifase");
        System.out.println("2) Mezcla Equilibrada");
        System.out.println("3) Distribucion");
        System.out.print("Opcion: ");
        opcionMetodo = entradas.nextInt();
        
        System.out.println("");
        System.out.println("En que orden lo quieres?");
        System.out.println("1) Ascendente");
        System.out.println("2) Descendente");
        System.out.print("Opcion: ");
        opcionOrden = entradas.nextInt();
        
        switch(opcionMetodo)
        {
            case 1:
                polifase(((opcionOrden - 1) == 1), numeroDatos);
                break;
            case 2:
                mezclaEquilibrada(((opcionOrden - 1) == 1), numeroDatos);
                break;
            case 3:
                distribucion(((opcionOrden - 1) == 1), numeroDatos);
                break;
        }
        
        /*
        El siguiente bloque copia la ultima linea de F0.txt, que es la linea ya
        ordenada
        */
        
        Path rutaDeF0 = Paths.get("F0.txt");
        Scanner leerLineaFinal = new Scanner(rutaDeF0);
        int numeroDeLineas = 0;
        while (leerLineaFinal.hasNextLine())
        {
            leerLineaFinal.nextLine();
            ++numeroDeLineas;
        }
        leerLineaFinal.close();
        leerLineaFinal = new Scanner(rutaDeF0);
        for (int k = 0; k < numeroDeLineas - 1; ++k)
            leerLineaFinal.nextLine();
        
        /*
        Aqui se crea un nuevo archivo con el nombre del archivo que se ordeno,
        agregandosele la palabra "Ordenado"
        */
        
        String nombreDeArchivoNuevo = archivosDisponibles.get(opcionArchivo - 1);
        String[] sinExtension = nombreDeArchivoNuevo.split("\\.");
        nombreDeArchivoNuevo = sinExtension[0] + "Ordenado.txt";
        
        FileWriter escribirArchivoNuevo = new FileWriter(nombreDeArchivoNuevo);
        leerLineaFinal.useDelimiter(",|'");
        while (leerLineaFinal.hasNextFloat())
        {
            Float temp = leerLineaFinal.nextFloat();
            
            if (!leerLineaFinal.hasNextFloat())
                escribirArchivoNuevo.write(temp.toString());
            else
                escribirArchivoNuevo.write(temp.toString() + ",");
        }
        
        leerLineaFinal.close();
        escribirArchivoNuevo.close();
        
        System.out.println("\nTerminado");
        
        System.out.println("Presiona ENTER para terminar...");
        try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}  
        
    }
    
    public static void polifase(boolean orden, int numeroDatos) throws IOException
    {
        /*
        Esta funcion asigna bloques de n/8 datos, los cuales son escritos alternadamente
        en F1.txt y F2.txt, y notese que solo es la primera parte donde todos los bloques
        iniciales se ordenan con cualquier algoritmo de ordenamiento (bubble sort en este caso)
        */
        
        ArrayList<Float> arrDatos = new ArrayList<>();
        Path rutaF0 = Paths.get("F0.txt");
        Scanner leerF0 = new Scanner(rutaF0);
        //El delimitador le indica al scanner que los numeros estan separados por comas
        leerF0.useDelimiter(",");
        FileWriter archivoF1 = new FileWriter("F1.txt");
        FileWriter archivoF2 = new FileWriter("F2.txt");
        boolean alternar = true;
        
        do
        {
            for (int i = 0; i < (numeroDatos / 8); ++i)
            {
                if (leerF0.hasNextFloat())
                    arrDatos.add(leerF0.nextFloat());
                else
                    break;
            }
            
            
            //Aqui es donde se ordenan con bubble sort los bloques
            ordenar(arrDatos, orden);
            
            //Aqui se alterna entre escribir un bloque en F1 o F2
            if (alternar)
            {
                archivoF1.write(arrDatos.get(0).toString());
                for (int i = 1; i < arrDatos.size(); ++i)
                {
                    archivoF1.write(",");
                    archivoF1.write(arrDatos.get(i).toString());
                }
                archivoF1.write("'");
                alternar = false;
            }
            else
            {
                archivoF2.write(arrDatos.get(0).toString());
                for (int i = 1; i < arrDatos.size(); ++i)
                {
                    archivoF2.write(",");
                    archivoF2.write(arrDatos.get(i).toString());
                }
                archivoF2.write("'");
                alternar = true;
            }
            
            arrDatos.clear();
            
        } while (leerF0.hasNextFloat());
        
        leerF0.close();
        archivoF1.close();
        archivoF2.close();
        
        //Esta es la segunda parte de polifase
        convergerBloques(orden);
    }
    
    private static void convergerBloques(boolean orden) throws IOException
    {
        /*
        PRECAUCION: Estas apunto de entrar a la funcion menos legible que hayas visto.
        
        Lo que se quiere lograr en esta funcion es que se lean los bloques que hay en
        F1 y F2, los cuales podrian tener forma de:
        Polifase
        F1 [4 4 4]
        F2 [4 4 0]
        o mezcla equilibrada
        F1 [3 5 1]
        F2 [6 2 4]
        y lo que se quiere lograr es que se combinen bloques mediante Merge Sort
        El problema es que cuando escribi esta funcion no sabia usar expresiones
        regulares, las cuales son de ayuda para leer informacion que nos interesa, 
        por lo que en el codigo se vera mucho el cambio de delimitadores entre comas
        y apostrofes
        */
        
        Path rutaF0 = Paths.get("F0.txt");
        Path rutaF1 = Paths.get("F1.txt");
        Path rutaF2 = Paths.get("F2.txt");
        
        Scanner bloquesF1 = new Scanner(rutaF1);
        bloquesF1.useDelimiter("");
        Scanner bloquesF2 = new Scanner(rutaF2);
        bloquesF2.useDelimiter("");
        
        int temp = 0;
        
        /*
        Estas listas almacenan el tamaño de los bloques de numeros ordenados,
        para que al combinar dos bloques, el programa sepa cuantos numeros esperar
        */
        ArrayList<Integer> numerosPorBloqueF1 = new ArrayList<>();
        ArrayList<Integer> numerosPorBloqueF2 = new ArrayList<>();
        ArrayList<Integer> numerosPorBloqueF0 = new ArrayList<>();
        
        while (bloquesF1.hasNext())
        {
            switch (bloquesF1.next())
            {
                //Si se detectan apostrofes, significa que el bloque termina
                case "'":
                    numerosPorBloqueF1.add(temp + 1);
                    temp = 0;
                    break;
                //Se añaden numeros al bloque actual
                case ",":
                    ++temp;
                    break;
            }
        }
        
        bloquesF1.close();
        
        while (bloquesF2.hasNext())
        {
            switch (bloquesF2.next())
            {
                case "'":
                    numerosPorBloqueF2.add(temp + 1);
                    temp = 0;
                    break;
                case ",":
                    ++temp;
                    break;
            }
        }
        
        bloquesF2.close();
        
        //En caso de que F1 tenga n bloques y F2 tenga n-1, esta condicional agrega un cero al final de F2
        if (numerosPorBloqueF1.size() != numerosPorBloqueF2.size())
            numerosPorBloqueF2.add(0);
        
        //Numero que determina apartir del numero de bloques de F1 o F2 cuantas iteraciones se necesitan
        int numeroDeIteraciones = (numerosPorBloqueF1.size() + 3) / 2;
        
        for (int i = 0; i < numeroDeIteraciones; ++i)
        {
            
            for (int j = 0; j < numerosPorBloqueF1.size(); ++j)
            {
                numerosPorBloqueF0.add(numerosPorBloqueF1.get(j) + numerosPorBloqueF2.get(j));
            }
            
            
            //Esta parte esta destinada a informar del estado actual de los bloques
            
            System.out.println("Iteracion :" + i);
            
            System.out.println("Bloques de F1:");
            System.out.print("[ ");
                for (Integer x : numerosPorBloqueF1)
            System.out.print(x + " ");
            System.out.println("]");
            
            System.out.println("Bloques de F2:");
            System.out.print("[ ");
            for (Integer x : numerosPorBloqueF2)
                System.out.print(x + " ");
            System.out.println("]");
            
            System.out.println("Bloques de F0:");
            System.out.print("[ ");
            for (Integer x : numerosPorBloqueF0)
                System.out.print(x + " ");
            System.out.println("]");
            System.out.println("");
            
            Scanner leerLineaF1 = new Scanner(rutaF1);
            leerLineaF1.useDelimiter(",");
            Scanner leerLineaF2 = new Scanner(rutaF2);
            leerLineaF2.useDelimiter(",");
            
            FileWriter escribirF0 = new FileWriter("F0.txt", true);
            
            /*
            Como el scanner empieza a leer desde el principio, esta funcion permite
            ir a la linea donde estan los datos mas recientes
            */
            
            for (int linea = 0; linea < i; ++linea)
            {
                leerLineaF1.nextLine();
                leerLineaF2.nextLine();
            }
            
            escribirF0.write(System.getProperty("line.separator"));
            
            for (int j = 0; j < numerosPorBloqueF1.size(); ++j) //Un ciclo donde se recorren todos los bloques de F1
            {
                /*
                Como los bloques estan ordenados, podemos meter archivos de F1 y F2 a distinas colas
                y desencolarlas con la seguridad de que estaran en orden
                */
                Queue<Float> miColaF1 = new LinkedList<>();
                Queue<Float> miColaF2 = new LinkedList<>();
                
                //Mientras haya archivos por leer
                while (numerosPorBloqueF1.get(j) + numerosPorBloqueF2.get(j) > 0)
                {
                    
                    //Condicional en caso de que ambos bloques contengan datos
                    if (numerosPorBloqueF1.get(j) > 0 && numerosPorBloqueF2.get(j) > 0)
                    {
                        
                        Float tempF1, tempF2;
                        
                        /*
                        En caso de que sobre solo un dato en F2, el scanner cambia de delimitadores para
                        prevenir que lea un caracter desconocido y se deje de funcionar el programa
                        */
                        if (numerosPorBloqueF1.get(j) > 1 && numerosPorBloqueF2.get(j) == 1)
                        {
                            leerLineaF2.useDelimiter("");
                            leerLineaF2.next();
                            leerLineaF2.useDelimiter("'");
                            
                            tempF1 = leerLineaF1.nextFloat();
                            tempF2 = leerLineaF2.nextFloat();
                            
                            miColaF1.add(tempF1);
                            miColaF2.add(tempF2);
                            
                            //Se desencola conforme al orden indicado
                            if (!orden)
                            {

                                if (miColaF1.element() < miColaF2.element())
                                {
                                    escribirF0.write(miColaF1.poll().toString() + ",");
                                }
                                else
                                {
                                    escribirF0.write(miColaF2.poll().toString() + ",");
                                }
                            
                            }
                            else
                            {
                                
                                if (miColaF1.element() > miColaF2.element())
                                {
                                    escribirF0.write(miColaF1.poll().toString() + ",");
                                }
                                else
                                {
                                    escribirF0.write(miColaF2.poll().toString() + ",");
                                }
                                
                            }
                            
                            //Se actualiza el estado de los bloques actuales de n a n-1
                            numerosPorBloqueF1.set(j, numerosPorBloqueF1.get(j) - 1);
                            numerosPorBloqueF2.set(j, numerosPorBloqueF2.get(j) - 1);
                            
                            leerLineaF2.useDelimiter("");
                            leerLineaF2.next();
                        }
                        /*
                        Lo mismo de arriba, pero ahora con F1 teniendo un elemento
                        */
                        else if (numerosPorBloqueF1.get(j) == 1 && numerosPorBloqueF2.get(j) > 1)
                        {
                            leerLineaF1.useDelimiter("");
                            leerLineaF1.next();
                            leerLineaF1.useDelimiter("'");
                            
                            tempF1 = leerLineaF1.nextFloat();
                            tempF2 = leerLineaF2.nextFloat();
                            
                            miColaF1.add(tempF1);
                            miColaF2.add(tempF2);
                            
                            if (!orden)
                            {
                                if (miColaF1.element() < miColaF2.element())
                                {
                                    escribirF0.write(miColaF1.poll().toString() + ",");
                                }
                                else
                                {
                                    escribirF0.write(miColaF2.poll().toString() + ",");
                                }
                            }
                            else
                            {
                                if (miColaF1.element() > miColaF2.element())
                                {
                                    escribirF0.write(miColaF1.poll().toString() + ",");
                                }
                                else
                                {
                                    escribirF0.write(miColaF2.poll().toString() + ",");
                                }
                            }
                            
                            numerosPorBloqueF1.set(j, numerosPorBloqueF1.get(j) - 1);
                            numerosPorBloqueF2.set(j, numerosPorBloqueF2.get(j) - 1);
                            
                            leerLineaF1.useDelimiter("");
                            
                            leerLineaF1.next();
                        }
                        
                        /*
                        Un caso donde se espera leer comas y apostrofes en ambos bloques
                        */
                        else if (numerosPorBloqueF1.get(j) == 1 && numerosPorBloqueF2.get(j) == 1)
                        {
                            leerLineaF1.useDelimiter("");
                            leerLineaF2.useDelimiter("");
                            leerLineaF1.next();
                            leerLineaF2.next();
                            leerLineaF1.useDelimiter("'");
                            leerLineaF2.useDelimiter("'");
                            
                            tempF1 = leerLineaF1.nextFloat();
                            tempF2 = leerLineaF2.nextFloat();
                            
                            miColaF1.add(tempF1);
                            miColaF2.add(tempF2);
                            
                            if (!orden)
                            {
                                if (miColaF1.element() < miColaF2.element())
                                {
                                    escribirF0.write(miColaF1.poll().toString() + ",");
                                }
                                else
                                {
                                    escribirF0.write(miColaF2.poll().toString() + ",");
                                }
                            }
                            else
                            {
                                if (miColaF1.element() > miColaF2.element())
                                {
                                    escribirF0.write(miColaF1.poll().toString() + ",");
                                }
                                else
                                {
                                    escribirF0.write(miColaF2.poll().toString() + ",");
                                }
                            }
                            
                            numerosPorBloqueF1.set(j, numerosPorBloqueF1.get(j) - 1);
                            numerosPorBloqueF2.set(j, numerosPorBloqueF2.get(j) - 1);
                            
                            leerLineaF1.useDelimiter("");
                            leerLineaF2.useDelimiter("");
                            
                            leerLineaF1.next();
                            leerLineaF2.next();
                        }
                        /*
                        Caso normal donde F1 y F2 tienen varios elementos del mismo bloque
                        por leer
                        */
                        else
                        {

                            tempF1 = leerLineaF1.nextFloat();
                            tempF2 = leerLineaF2.nextFloat();
                            
                            miColaF1.add(tempF1);
                            miColaF2.add(tempF2);
                            
                            if (!orden)
                            {
                                if (miColaF1.element() < miColaF2.element())
                                {
                                    escribirF0.write(miColaF1.poll().toString() + ",");
                                }
                                else
                                {
                                    escribirF0.write(miColaF2.poll().toString() + ",");
                                }
                            }
                            else
                            {
                                if (miColaF1.element() > miColaF2.element())
                                {
                                    escribirF0.write(miColaF1.poll().toString() + ",");
                                }
                                else
                                {
                                    escribirF0.write(miColaF2.poll().toString() + ",");
                                }
                            }

                            numerosPorBloqueF1.set(j, numerosPorBloqueF1.get(j) - 1);
                            numerosPorBloqueF2.set(j, numerosPorBloqueF2.get(j) - 1);

                        }
                        
                    }
                    /*
                    En caso de que solo un bloque tenga archivos, se tiene la seguridad
                    de que todos estan ordenados, por lo que solo se copian directo del bloque
                    */
                    else if (numerosPorBloqueF1.get(j) > 0)
                    {
                        
                        Float tempF1;
                        
                        if (numerosPorBloqueF1.get(j) == 1)
                        {
                            leerLineaF1.useDelimiter("");
                            leerLineaF1.next();
                            leerLineaF1.useDelimiter("'");
                            
                            tempF1 = leerLineaF1.nextFloat();
                            
                            miColaF1.add(tempF1);
                            
                            numerosPorBloqueF1.set(j, numerosPorBloqueF1.get(j) - 1);
                            
                            leerLineaF1.useDelimiter("");
                            
                            leerLineaF1.next();
                        }
                        else
                        {
                            tempF1 = leerLineaF1.nextFloat();
                            
                            miColaF1.add(tempF1);

                            numerosPorBloqueF1.set(j, numerosPorBloqueF1.get(j) - 1);

                        }
                        
                    }
                    //Lo mismo de antes
                    else
                    {
                        
                        Float tempF2;
                        
                        if (numerosPorBloqueF2.get(j) == 1)
                        {
                            leerLineaF2.useDelimiter("");
                            leerLineaF2.next();
                            leerLineaF2.useDelimiter("'");
                            
                            tempF2 = leerLineaF2.nextFloat();
                            
                            miColaF2.add(tempF2);
                            
                            numerosPorBloqueF2.set(j, numerosPorBloqueF2.get(j) - 1);
                            
                            leerLineaF2.useDelimiter("");
                            
                            leerLineaF2.next();
                        }
                        else
                        {
                            tempF2 = leerLineaF2.nextFloat();

                            miColaF2.add(tempF2);

                            numerosPorBloqueF2.set(j, numerosPorBloqueF2.get(j) - 1);
                        }
                    }
                    
                    leerLineaF1.useDelimiter(",");
                    leerLineaF2.useDelimiter(",");
                    
                    
                }
                
                /*
                Si hay elementos restantes en la cola, entonces se desencolan y agregan a F0
                */
                while (!miColaF1.isEmpty() || !miColaF2.isEmpty())
                {
                    if (!miColaF1.isEmpty() && !miColaF2.isEmpty())
                    {
                        if (!orden)
                        {
                            if (miColaF1.element() < miColaF2.element())
                                escribirF0.write(miColaF1.poll().toString() + ",");
                            else
                                escribirF0.write(miColaF2.poll().toString() + ",");
                        }
                        else
                        {
                            if (miColaF1.element() > miColaF2.element())
                                escribirF0.write(miColaF1.poll().toString() + ",");
                            else
                                escribirF0.write(miColaF2.poll().toString() + ",");
                        }
                    }
                    else if (miColaF1.size() > 1)
                    {
                        escribirF0.write(miColaF1.poll().toString() + ",");
                    }
                    else if (miColaF2.size() > 1)
                    {
                        escribirF0.write(miColaF2.poll().toString() + ",");
                    }
                    else if (miColaF1.size() == 1)
                    {
                        escribirF0.write(miColaF1.poll().toString());
                    }
                    else
                    {
                        escribirF0.write(miColaF2.poll().toString());
                    }
                                
                }
                
                escribirF0.write("'");
                
            }
            
            escribirF0.flush();
            escribirF0.close();
            leerLineaF1.close();
            leerLineaF2.close();
            
            //Si F0 solo tiene un bloque, el algoritmo ya ordeno todos los datos
            if (numerosPorBloqueF0.size() == 1)
            {
                return;
            }
            
            numerosPorBloqueF1.clear();
            numerosPorBloqueF2.clear();
            
            /*
            Como en F0 hay varios bloques combinados de F1 y F2, entonces aqui
            se separan nuevamente en F1 y F2
            */
            for (int j = 0; j < numerosPorBloqueF0.size(); ++j)
            {
                if (j % 2 == 0)
                    numerosPorBloqueF1.add(numerosPorBloqueF0.get(j));
                else
                    numerosPorBloqueF2.add(numerosPorBloqueF0.get(j));
            }
            
            if (numerosPorBloqueF1.size() != numerosPorBloqueF2.size())
                numerosPorBloqueF2.add(0);
            
            Scanner leerLineaF0 = new Scanner(rutaF0);
            for (int linea = 0; linea < i + 1; ++linea)
            {
                leerLineaF0.nextLine();
            }
            
            
            FileWriter escribirF1 = new FileWriter("F1.txt", true);
            FileWriter escribirF2 = new FileWriter("F2.txt", true);
            
            escribirF1.write(System.getProperty("line.separator"));
            escribirF2.write(System.getProperty("line.separator"));
            
            
            //Aqui ocurre la separacion
            
            for (int j = 0; j < numerosPorBloqueF0.size(); ++j)
            {
                if (j % 2 == 0)
                {
                    
                    leerLineaF0.useDelimiter(",");
                    
                    for (int k = 0; k < numerosPorBloqueF0.get(j); ++k)
                    {
                        
                        Float tempF0;
                        
                        if (k == (numerosPorBloqueF0.get(j) - 1))
                        {
                            
                            leerLineaF0.useDelimiter("");
                            leerLineaF0.next();
                            leerLineaF0.useDelimiter("'");
                            
                            tempF0 = leerLineaF0.nextFloat();
                            
                            escribirF1.write(tempF0.toString());
                            leerLineaF0.useDelimiter("");
                            leerLineaF0.next();
                            leerLineaF0.useDelimiter(",");
                            
                        }
                        else
                        {
                            tempF0 = leerLineaF0.nextFloat();
                        
                            escribirF1.write(tempF0.toString() + ",");
                        }
                        
                    }
                    
                    escribirF1.write("'");
                    
                }
                else
                {
                    
                    leerLineaF0.useDelimiter(",");
                    
                    for (int k = 0; k < numerosPorBloqueF0.get(j); ++k)
                    {
                        
                        Float tempF0;
                        
                        if (k == numerosPorBloqueF0.get(j) - 1)
                        {
                            leerLineaF0.useDelimiter("");
                            leerLineaF0.next();
                            leerLineaF0.useDelimiter("'");
                            
                            tempF0 = leerLineaF0.nextFloat();
                            
                            escribirF2.write(tempF0.toString());
                            
                            leerLineaF0.useDelimiter("");
                            leerLineaF0.next();
                        }
                        else
                        {
                            tempF0 = leerLineaF0.nextFloat();
                        
                            escribirF2.write(tempF0.toString() + ",");
                        }
                        
                    }
                    
                    escribirF2.write("'");
                    
                }
            }
            
            leerLineaF0.close();
            escribirF1.close();
            escribirF2.close();
            numerosPorBloqueF0.clear();
            
        }
        
    }
    
    public static void mezclaEquilibrada(boolean orden, int numeroDatos) throws IOException
    {
        
        //Esta funcion consiste en crear bloques que esten ya ordenados
        
        ArrayList<Float> arrDatos = new ArrayList<>();
        Path rutaF0 = Paths.get("F0.txt");
        Scanner leerF0 = new Scanner(rutaF0);
        leerF0.useDelimiter(",");
        FileWriter archivoF1 = new FileWriter("F1.txt");
        FileWriter archivoF2 = new FileWriter("F2.txt");
        boolean alternar = true;
        boolean bloqueEspecial = false;
        
        //El programa empieza leyendo un numero, ya que este se comparara iniciando el ciclo
        Float temp = leerF0.nextFloat();
        
        do
        {

            arrDatos.add(temp);
            /*
            En este ciclo se decide si un bloque sigue creciendo o si el ultimo
            numero leido no esta ordenado con respecto a los demas
            */
            for (int i = 0; leerF0.hasNextFloat(); ++i)
            {
                temp = leerF0.nextFloat();
                
                if (!orden)
                {
                    if (arrDatos.get(i) < temp)
                        arrDatos.add(temp);
                    else
                        break;
                }
                else
                {
                    if (arrDatos.get(i) > temp)
                        arrDatos.add(temp);
                    else
                        break;
                }
            }
            
            if (alternar)
            {
                archivoF1.write(arrDatos.get(0).toString());
                for (int i = 1; i < arrDatos.size(); ++i)
                {
                    archivoF1.write(",");
                    archivoF1.write(arrDatos.get(i).toString());
                }
                archivoF1.write("'");
                alternar = false;
                
                if (bloqueEspecial)
                    bloqueEspecial = false;
            }
            else
            {
                archivoF2.write(arrDatos.get(0).toString());
                for (int i = 1; i < arrDatos.size(); ++i)
                {
                    archivoF2.write(",");
                    archivoF2.write(arrDatos.get(i).toString());
                }
                archivoF2.write("'");
                alternar = true;
                
                if (bloqueEspecial)
                    bloqueEspecial = false;
            }
            
            /*
            En caso de que se hayan leido todos los numeros y solo haya uno
            en la variable temporal, se activa el "Caso especial" que consiste
            en asignarle un bloque solo para ese numero
            */
            if (!leerF0.hasNextFloat())
                if (arrDatos.get(arrDatos.size() - 1) != temp)
                {
                    System.out.println("Caso especial");
                    bloqueEspecial = true;
                }
            
            arrDatos.clear();
            
        } while (leerF0.hasNextFloat() || bloqueEspecial);
        
        leerF0.close();
        archivoF1.close();
        archivoF2.close();
        /*
        Mezcla equilibrada tambien hace uso de converger bloques, ya que este
        ordenamiento y Polifase tienen la misma logica de usar Merge sort
        */
        convergerBloques(orden);
        
    }
    
    public static void distribucion(boolean orden, int numeroDatos) throws IOException
    {
        /*
        En distribucion, se crea un archivo para cada numero que hay
        y a diferencia de los otros algoritmos, este puede tener bloques
        vacios en determinados momentos
        */
        
        Path rutaFControl = Paths.get("F0.txt");
        Path rutaF0 = Paths.get("F_0.txt");
        Path rutaF1 = Paths.get("F_1.txt");
        Path rutaF2 = Paths.get("F_2.txt");
        Path rutaF3 = Paths.get("F_3.txt");
        Path rutaF4 = Paths.get("F_4.txt");
        Path rutaF5 = Paths.get("F_5.txt");
        Path rutaF6 = Paths.get("F_6.txt");
        Path rutaF7 = Paths.get("F_7.txt");
        Path rutaF8 = Paths.get("F_8.txt");
        Path rutaF9 = Paths.get("F_9.txt");
        
        FileWriter[] inicializarArchivos =
        {
            new FileWriter("F_0.txt"),
            new FileWriter("F_1.txt"),
            new FileWriter("F_2.txt"),
            new FileWriter("F_3.txt"),
            new FileWriter("F_4.txt"),
            new FileWriter("F_5.txt"),
            new FileWriter("F_6.txt"),
            new FileWriter("F_7.txt"),
            new FileWriter("F_8.txt"),
            new FileWriter("F_9.txt")
        };
        
        for (FileWriter fw : inicializarArchivos)
            fw.close();
        
        Scanner recorrerFControl = new Scanner(rutaFControl);
        recorrerFControl.useDelimiter(",");
        String[] decimalesYEnteros;
        
        int maxNumeros = 0;
        int maxDecimales = 0;
        int maxEnteros = 0;
        
        /*
        Este ciclo tiene como objetivo detectar el maximo numero de enteros, decimales
        y la suma de estos, con el objetivo de calcular el numero de iteraciones
        que se necesitan para ordenar los numeros
        */
        
        do
        {
            Float temp = recorrerFControl.nextFloat();
            decimalesYEnteros = temp.toString().split("\\.");
            
            if (decimalesYEnteros[0].length() > maxEnteros)
                maxEnteros = decimalesYEnteros[0].length();
            if (decimalesYEnteros[1].length() > maxDecimales)
                maxDecimales = decimalesYEnteros[1].length();
            
        } while (recorrerFControl.hasNextFloat());
        
        maxNumeros = maxDecimales + maxEnteros;
        
        System.out.println("");
        System.out.println("Maximo numero de decimales: " + maxDecimales);
        System.out.println("Maximo numero de enteros: " + maxEnteros);
        System.out.println("Maximo numero de digitos: " + maxNumeros);
        
        recorrerFControl.close();
        
        ArrayList<Integer> numerosPorArchivo = new ArrayList<>();
        
        for (int i = 0; i < maxNumeros; ++i)
        {
            
            Scanner leerFControl = new Scanner(rutaFControl);
            /*
            Este delimitador puede leer numeros entre comillas o apostrofes
            lo que se conoce como expresiones regulares (regex)
            */
            leerFControl.useDelimiter(",|'");
            
            for (int j = 0; j < i; ++j)
                leerFControl.nextLine();
            
            /*
            Aqui se detecta desde que linea se tienen que desencolar
            los numeros
            */
            
            FileWriter[] escrituraDeArchivos =
            {
                new FileWriter("F_0.txt", true),
                new FileWriter("F_1.txt", true),
                new FileWriter("F_2.txt", true),
                new FileWriter("F_3.txt", true),
                new FileWriter("F_4.txt", true),
                new FileWriter("F_5.txt", true),
                new FileWriter("F_6.txt", true),
                new FileWriter("F_7.txt", true),
                new FileWriter("F_8.txt", true),
                new FileWriter("F_9.txt", true)
            };
            
            numerosPorArchivo.clear();
            
            for (int j = 0; j < 10; ++j)
                numerosPorArchivo.add(0);
            
            /*
            El objetivo de este ciclo es añadir ceros a la izquierda de un numero si este
            no tiene el mismo numero de enteros que el numero con mas enteros, y tambien añade
            ceros a la derecha si el numero de decimales no es igual que el numero con mas decimales,
            dando numeros con el mismo numero de caracteres sin punto decimal
            */
            for (int j = 0; j < numeroDatos; ++j)
            {
                Float lectura = leerFControl.nextFloat();
                decimalesYEnteros = lectura.toString().split("\\.");
                String str = decimalesYEnteros[0] + decimalesYEnteros[1];
                
                if (decimalesYEnteros[0].length() < maxEnteros)
                    for (int k = decimalesYEnteros[0].length(); k < maxEnteros; ++k)
                        str = "0" + str;
                if (decimalesYEnteros[1].length() < maxDecimales)
                    for (int k = decimalesYEnteros[1].length(); k < maxDecimales; ++k)
                        str = str + "0";
                
                numerosPorArchivo.set(str.charAt(maxNumeros - 1 - i) - '0', numerosPorArchivo.get(str.charAt(maxNumeros - 1 - i) - '0') + 1);

                escrituraDeArchivos[str.charAt(maxNumeros - 1 - i) - '0'].write(lectura.toString() + ",");

            }
            
            leerFControl.close();
            
            for (FileWriter f : escrituraDeArchivos)
            {
                f.write(System.getProperty("line.separator"));
                f.close();
            }
            
            Scanner[] lecturaDeArchivos =
            {
                new Scanner(rutaF0),
                new Scanner(rutaF1),
                new Scanner(rutaF2),
                new Scanner(rutaF3),
                new Scanner(rutaF4),
                new Scanner(rutaF5),
                new Scanner(rutaF6),
                new Scanner(rutaF7),
                new Scanner(rutaF8),
                new Scanner(rutaF9),
            };
            
            for (int j = 0; j < i; ++j)
            {
                for (Scanner s : lecturaDeArchivos)
                {
                    s.nextLine();
                }
            }
            
            for (Scanner s : lecturaDeArchivos)
                s.useDelimiter(",");
            /*
            Se informa al usuario de las iteraciones en el digito acutal,
            para saber cuantos bloques de cada archivo se llenaron
            */
            System.out.println("\nIteracion del digito " + (maxNumeros - i - 1));
            
            System.out.println("Numeros por bloque :");
            System.out.print("[ ");
            for (Integer j : numerosPorArchivo)
                System.out.print(j + " ");
            System.out.println("]");
            
            FileWriter escribirFControl = new FileWriter("F0.txt", true);
            escribirFControl.write(System.getProperty("line.separator"));
            
            /*
            Aqui se decide en que orden se desencolan los archivos, si de
            0 a 9 para que sean ascendentes, o de 9 a 0 para que estos sean
            descencientes
            */
            
            if (!orden)
            {
                for (int j = 0; j < 10; ++j)
                {

                    for (int k = 0; k < numerosPorArchivo.get(j); ++k)
                    {

                        if (k != (numerosPorArchivo.get(j) - 1))
                        {
                            Float temp = lecturaDeArchivos[j].nextFloat();
                            escribirFControl.write(temp.toString() + ",");
                        }
                        else
                        {
                            Float temp = lecturaDeArchivos[j].nextFloat();
                            escribirFControl.write(temp.toString() + "'");
                        }

                    }

                }
            }
            else
            {
                for (int j = 10 - 1; j >= 0; --j)
                {

                    for (int k = 0; k < numerosPorArchivo.get(j); ++k)
                    {

                        if (k != (numerosPorArchivo.get(j) - 1))
                        {
                            Float temp = lecturaDeArchivos[j].nextFloat();
                            escribirFControl.write(temp.toString() + ",");
                        }
                        else
                        {
                            Float temp = lecturaDeArchivos[j].nextFloat();
                            escribirFControl.write(temp.toString() + "'");
                        }

                    }

                }
            }
            
            escribirFControl.close();
            
            for (Scanner s : lecturaDeArchivos)
            {
                s.close();
            }
            
        }
        
    }
    
    public static void ordenar(ArrayList<Float> lista, boolean orden)
    {
        /*
        Se uso bubble sort por facilidad
        */
        if (!orden)
        {
            for (int i = 0; i < lista.size(); ++i)
            {
                for (int j = 0; j < lista.size() - 1; ++j)
                {
                    if (lista.get(j) > lista.get(j + 1))
                    {
                        float temp = lista.get(j);
                        lista.set(j, lista.get(j + 1));
                        lista.set(j + 1, temp);
                    }
                }
            }
        }
        
        else
        {
            for (int i = 0; i < lista.size(); ++i)
            {
                for (int j = 0; j < lista.size() - 1; ++j)
                {
                    if (lista.get(j) < lista.get(j + 1))
                    {
                        float temp = lista.get(j);
                        lista.set(j, lista.get(j + 1));
                        lista.set(j + 1, temp);
                    }
                }
            }
        }
        
    }
    
    public static void generarNumeros() throws IOException
    {
        Random rand = new Random();
        FileWriter numerosAleatorios = new FileWriter("numerosAleatorios.txt");
        for (int i = 0; i < 10000 - 1; ++i)
        {
            Float aa = rand.nextFloat() * (100);
            numerosAleatorios.write(aa.toString() + ",");
        }
        Float aa = rand.nextFloat();
        numerosAleatorios.write(aa.toString());
        numerosAleatorios.flush();
        numerosAleatorios.close();
    }
    
    public static void eliminarResiduos()
    {
        File folder = new File("./");
        File[] listaDeArchivos = folder.listFiles();
        
        ArrayList<String> archivosAuxiliares = new ArrayList<>();
        archivosAuxiliares.add("F0.txt");
        archivosAuxiliares.add("F1.txt");
        archivosAuxiliares.add("F2.txt");
        archivosAuxiliares.add("F_0.txt");
        archivosAuxiliares.add("F_1.txt");
        archivosAuxiliares.add("F_2.txt");
        archivosAuxiliares.add("F_3.txt");
        archivosAuxiliares.add("F_4.txt");
        archivosAuxiliares.add("F_5.txt");
        archivosAuxiliares.add("F_6.txt");
        archivosAuxiliares.add("F_7.txt");
        archivosAuxiliares.add("F_8.txt");
        archivosAuxiliares.add("F_9.txt");
        
        for (File f : listaDeArchivos)
        {
            if (archivosAuxiliares.contains(f.getName()))
            {
                archivosAuxiliares.remove(f.getName());
                f.delete();
            }
        }
    }
    
}
