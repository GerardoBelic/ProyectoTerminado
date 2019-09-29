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

public class PruebaProyecto {

    public static void main(String[] args) throws IOException {
        
        generarNumeros();
        
        eliminarResiduos();
        
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
        
        while(leerArchivoOriginal.hasNextLine())
        {
            copiarArchivoOriginal.write(leerArchivoOriginal.nextLine());   //En teoria solo deberia copiar una linea
        }

        copiarArchivoOriginal.flush();
        copiarArchivoOriginal.close();
        leerArchivoOriginal.close();
        
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
        
    }
    
    public static void polifase(boolean orden, int numeroDatos) throws IOException
    {
        ArrayList<Float> arrDatos = new ArrayList<>();
        Path rutaF0 = Paths.get("F0.txt");
        Scanner leerF0 = new Scanner(rutaF0);
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
            
            ordenar(arrDatos, orden);
            
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
        
        convergerBloques(orden);
    }
    
    private static void convergerBloques(boolean orden) throws IOException
    {
        
        Path rutaF0 = Paths.get("F0.txt");
        Path rutaF1 = Paths.get("F1.txt");
        Path rutaF2 = Paths.get("F2.txt");
        
        Scanner bloquesF1 = new Scanner(rutaF1);
        bloquesF1.useDelimiter("");
        Scanner bloquesF2 = new Scanner(rutaF2);
        bloquesF2.useDelimiter("");
        
        int temp = 0;
        
        ArrayList<Integer> numerosPorBloqueF1 = new ArrayList<>();
        ArrayList<Integer> numerosPorBloqueF2 = new ArrayList<>();
        ArrayList<Integer> numerosPorBloqueF0 = new ArrayList<>();
        
        while (bloquesF1.hasNext())
        {
            switch (bloquesF1.next())
            {
                case "'":
                    numerosPorBloqueF1.add(temp + 1);
                    temp = 0;
                    break;
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
        
        if (numerosPorBloqueF1.size() != numerosPorBloqueF2.size())
            numerosPorBloqueF2.add(0);
        
        int numeroDeIteraciones = (numerosPorBloqueF1.size() + 3) / 2;
        
        for (int i = 0; i < numeroDeIteraciones; ++i)
        {
            
            for (int j = 0; j < numerosPorBloqueF1.size(); ++j)
            {
                numerosPorBloqueF0.add(numerosPorBloqueF1.get(j) + numerosPorBloqueF2.get(j));
            }
            
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
            
            for (int linea = 0; linea < i; ++linea)
            {
                leerLineaF1.nextLine();
                leerLineaF2.nextLine();
            }
            
            escribirF0.write(System.getProperty("line.separator"));
            
            for (int j = 0; j < numerosPorBloqueF1.size(); ++j) //Un ciclo donde se recorren todos los bloques de F1
            {
                
                Queue<Float> miColaF1 = new LinkedList<>();
                Queue<Float> miColaF2 = new LinkedList<>();
                
                while (numerosPorBloqueF1.get(j) + numerosPorBloqueF2.get(j) > 0)
                {
                    
                    if (numerosPorBloqueF1.get(j) > 0 && numerosPorBloqueF2.get(j) > 0)
                    {
                        
                        Float tempF1, tempF2;
                        
                        if (numerosPorBloqueF1.get(j) > 1 && numerosPorBloqueF2.get(j) == 1)
                        {
                            leerLineaF2.useDelimiter("");
                            leerLineaF2.next();
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
                            
                            leerLineaF2.useDelimiter("");
                            leerLineaF2.next();
                        }
                        
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
            
            if (numerosPorBloqueF0.size() == 1)
            {
                return;
            }
            
            numerosPorBloqueF1.clear();
            numerosPorBloqueF2.clear();
            
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
            
            //boolean alternar = true;
            FileWriter escribirF1 = new FileWriter("F1.txt", true);
            FileWriter escribirF2 = new FileWriter("F2.txt", true);
            
            escribirF1.write(System.getProperty("line.separator"));
            escribirF2.write(System.getProperty("line.separator"));
            
            //System.out.println("numeros por bloque f0 " + numerosPorBloqueF0.size());
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
        
        ArrayList<Float> arrDatos = new ArrayList<>();
        Path rutaF0 = Paths.get("F0.txt");
        Scanner leerF0 = new Scanner(rutaF0);
        leerF0.useDelimiter(",");
        FileWriter archivoF1 = new FileWriter("F1.txt");
        FileWriter archivoF2 = new FileWriter("F2.txt");
        boolean alternar = true;
        boolean bloqueEspecial = false;
        
        Float temp = leerF0.nextFloat();
        
        do
        {

            arrDatos.add(temp);
            
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
        
        convergerBloques(orden);
        
    }
    
    public static void distribucion(boolean orden, int numeroDatos) throws IOException
    {
        
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
            leerFControl.useDelimiter(",|'");
            
            for (int j = 0; j < i; ++j)
                leerFControl.nextLine();
            
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
            
            System.out.println("\nIteracion del digito " + (maxNumeros - i - 1));
            
            System.out.println("Numeros por bloque :");
            System.out.print("[ ");
            for (Integer j : numerosPorArchivo)
                System.out.print(j + " ");
            System.out.println("]");
            
            FileWriter escribirFControl = new FileWriter("F0.txt", true);
            escribirFControl.write(System.getProperty("line.separator"));
            
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
        for (int i = 0; i < 20 - 1; ++i)
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
