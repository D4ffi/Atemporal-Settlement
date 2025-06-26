package com.bydaffi.atemp;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CampStructure {

    /**
     * Genera la estructura del campamento en la posición especificada
     * @param world El mundo donde generar la estructura
     * @param fogataPos La posición central donde colocar la fogata (centro de la estructura)
     */
    public static void generarEstructuraCampamento(World world, BlockPos fogataPos) {
        // La fogata ya existe, usamos su posición como centro

        // Generar la base de 3x3 debajo de la fogata
        generarBase(world, fogataPos);

        // Colocar bedrock temporal como "fogata ancla"
        colocarFogataAncla(world, fogataPos);

        // Generar las paredes del perímetro 3x3 (altura 3)
        generarParedes(world, fogataPos);

        // Generar el techo duplicando la base a altura 4
        generarTecho(world, fogataPos);

        // Crear el doorway en una de las paredes
        crearDoorway(world, fogataPos);
    }


    private static void generarBase(World world, BlockPos centro) {
        // Base 3x3 debajo de la fogata
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = centro.add(x, -1, z);
                world.setBlockState(pos, Blocks.SPRUCE_PLANKS.getDefaultState());
            }
        }
    }


    private static void colocarFogataAncla(World world, BlockPos centro) {
        // Colocar bedrock temporal como "fogata ancla" en el centro
        world.setBlockState(centro, Blocks.BEDROCK.getDefaultState());
    }


    private static void generarParedes(World world, BlockPos centro) {
        // Generar paredes en el perímetro 5x5, dejando las esquinas vacías
        for (int y = 0; y <= 2; y++) {
            // Pared norte (z = -2) - 3 bloques centrales, esquinas vacías
            world.setBlockState(centro.add(-1, y, -2), Blocks.WHITE_WOOL.getDefaultState());
            world.setBlockState(centro.add(0, y, -2), Blocks.WHITE_WOOL.getDefaultState());
            world.setBlockState(centro.add(1, y, -2), Blocks.WHITE_WOOL.getDefaultState());
            
            // Pared sur (z = 2) - 3 bloques centrales, esquinas vacías
            world.setBlockState(centro.add(-1, y, 2), Blocks.WHITE_WOOL.getDefaultState());
            world.setBlockState(centro.add(0, y, 2), Blocks.WHITE_WOOL.getDefaultState());
            world.setBlockState(centro.add(1, y, 2), Blocks.WHITE_WOOL.getDefaultState());
            
            // Pared este (x = 2) - 3 bloques centrales, esquinas vacías
            world.setBlockState(centro.add(2, y, -1), Blocks.WHITE_WOOL.getDefaultState());
            world.setBlockState(centro.add(2, y, 0), Blocks.WHITE_WOOL.getDefaultState());
            world.setBlockState(centro.add(2, y, 1), Blocks.WHITE_WOOL.getDefaultState());
            
            // Pared oeste (x = -2) - 3 bloques centrales, esquinas vacías
            world.setBlockState(centro.add(-2, y, -1), Blocks.WHITE_WOOL.getDefaultState());
            world.setBlockState(centro.add(-2, y, 0), Blocks.WHITE_WOOL.getDefaultState());
            world.setBlockState(centro.add(-2, y, 1), Blocks.WHITE_WOOL.getDefaultState());
        }
    }


    private static void generarTecho(World world, BlockPos centro) {
        int alturaTecho = 3; // 3 bloques arriba del centro

        // Duplicar la base a altura 4 (techo completo 3x3)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos techoPos = centro.add(x, alturaTecho, z);
                world.setBlockState(techoPos, Blocks.WHITE_WOOL.getDefaultState());
            }
        }
    }


    private static void crearDoorway(World world, BlockPos centro) {
        // Crear doorway en la pared oeste (x = -2) en el perímetro 5x5
        // Doorway de 1 bloque de ancho y 2 de alto
        
        BlockPos doorway1 = centro.add(-2, 0, 0);
        BlockPos doorway2 = centro.add(-2, 1, 0);

        world.setBlockState(doorway1, Blocks.AIR.getDefaultState());
        world.setBlockState(doorway2, Blocks.AIR.getDefaultState());
    }


    public static void spawnCamp(World world, BlockPos fogataPos) {
        // Usar directamente la posición de la fogata como centro
        generarEstructuraCampamento(world, fogataPos);
    }


    public static boolean puedeGenerarEstructura(World world, BlockPos centro) {
        // Verificar un área de 3x3x5 (incluyendo el techo)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y <= 4; y++) {
                    BlockPos checkPos = centro.add(x, y, z);

                    // Si y = 0, verificar que sea un bloque sólido (para la base)
                    if (y == 0) {
                        if (!world.getBlockState(checkPos.down()).isSolidBlock(world, checkPos.down())) {
                            return false; // No hay base sólida
                        }
                    } else {
                        // Para otros niveles, verificar que esté vacío o sea reemplazable
                        if (!world.getBlockState(checkPos).isAir() &&
                                !world.getBlockState(checkPos).getBlock().equals(Blocks.GRASS) &&
                                !world.getBlockState(checkPos).getBlock().equals(Blocks.TALL_GRASS)) {
                            return false; // Hay obstáculos
                        }
                    }
                }
            }
        }
        return true;
    }

}
