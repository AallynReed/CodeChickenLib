package codechicken.lib.fluid;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class FluidUtils {

    public static int B = FluidType.BUCKET_VOLUME;

    public static int getLuminosity(FluidStack stack, double density) {
        if (stack.isEmpty()) {
            return 0;
        }
        Fluid fluid = stack.getFluid();
        FluidType type = fluid.getFluidType();
        int light = type.getLightLevel(stack);
        if (type.isLighterThanAir()) {
            light = (int) (light * density);
        }
        return light;
    }
}
