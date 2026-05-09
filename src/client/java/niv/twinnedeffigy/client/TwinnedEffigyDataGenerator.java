package niv.twinnedeffigy.client;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;
import static net.minecraft.client.data.models.blockstates.MultiVariantGenerator.dispatch;
import static net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction.copyComponentsFromBlockEntity;

import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NullMarked;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext.BlockEntityTarget;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import niv.twinnedeffigy.TwinnedEffigy;
import niv.twinnedeffigy.block.entity.TwinnedEffigyBlockEntity;
import niv.twinnedeffigy.item.TwinnedEffigyItem;
import niv.twinnedeffigy.registry.ModBlockEntityTypes;
import niv.twinnedeffigy.registry.ModBlocks;
import niv.twinnedeffigy.registry.ModItems;

@NullMarked
public class TwinnedEffigyDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        ModBlocks.initialize();
        ModItems.initialize();
        ModBlockEntityTypes.initialize();

        var pack = fabricDataGenerator.createPack();

        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModEnglishLanguageProvider::new);
        pack.addProvider(ModBlockLootTableProvider::new);
        pack.addProvider(ModBlockTagsProvider::new);
    }

    private static final class ModModelProvider extends FabricModelProvider {

        public ModModelProvider(FabricPackOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generators) {
            var model = ModelLocationUtils.getModelLocation(Blocks.CHISELED_STONE_BRICKS);
            generators.blockStateOutput.accept(dispatch(ModBlocks.TWINNED_EFFIGY, plainVariant(model)));
            generators.registerSimpleItemModel(ModBlocks.TWINNED_EFFIGY, model);
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerators) {
            // no item models to generate
        }
    }

    private static final class ModEnglishLanguageProvider extends FabricLanguageProvider {

        protected ModEnglishLanguageProvider(FabricPackOutput packOutput, CompletableFuture<Provider> registryLookup) {
            super(packOutput, registryLookup);
        }

        @Override
        public void generateTranslations(Provider registries, TranslationBuilder builder) {
            builder.add(ModItems.CREATIVE_TAB_NAME, TwinnedEffigy.MOD_NAME);

            var nameOfTwinnedEffigy = "Twinned Effigy";

            builder.add(ModBlocks.TWINNED_EFFIGY, nameOfTwinnedEffigy);
            builder.add(ModItems.TWINNED_EFFIGY, nameOfTwinnedEffigy);
            builder.add(TwinnedEffigyBlockEntity.CONTAINER_NAME, nameOfTwinnedEffigy);
            builder.add(TwinnedEffigyItem.BOUND_NAME, "%s's " + nameOfTwinnedEffigy);
        }
    }

    private static final class ModBlockLootTableProvider extends FabricBlockLootSubProvider {

        protected ModBlockLootTableProvider(FabricPackOutput packOutput, CompletableFuture<Provider> registriesFuture) {
            super(packOutput, registriesFuture);
        }

        @SuppressWarnings("null")
        @Override
        public void generate() {
            add(ModBlocks.TWINNED_EFFIGY, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.TWINNED_EFFIGY)
                                    .when(hasSilkTouch())
                                    .apply(copyComponentsFromBlockEntity(BlockEntityTarget.BLOCK_ENTITY.contextParam())
                                            .include(DataComponents.LOCK)
                                            .include(DataComponents.PROFILE))
                                    .otherwise(LootItem.lootTableItem(ModItems.TWINNED_EFFIGY))))
                    .setRandomSequence(BuiltInRegistries.BLOCK.getKey(ModBlocks.TWINNED_EFFIGY).withPrefix("blocks/")));
        }

    }

    private static final class ModBlockTagsProvider extends FabricTagsProvider<Block> {

        public ModBlockTagsProvider(FabricPackOutput output, CompletableFuture<Provider> registryLookupFuture) {
            super(output, Registries.BLOCK, registryLookupFuture);
        }

        @SuppressWarnings("java:S4449")
        @Override
        protected void addTags(Provider registries) {
            builder(BlockTags.MINEABLE_WITH_PICKAXE)
                    .addOptional(BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.TWINNED_EFFIGY).orElse(null))
                    .setReplace(false);
        }
    }
}
