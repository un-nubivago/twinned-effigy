package niv.twinnedeffigy.client;

import static net.minecraft.client.data.models.model.ModelTemplates.CUBE_TOP;
import static net.minecraft.client.data.models.model.TextureSlot.SIDE;
import static net.minecraft.client.data.models.model.TexturedModel.COLUMN;
import static niv.twinnedeffigy.block.TwinnedEffigyBlock.LIT;

import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NullMarked;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext.BlockEntityTarget;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
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
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModBlockTagsProvider::new);
    }

    private static final class ModModelProvider extends FabricModelProvider {

        public ModModelProvider(FabricPackOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generators) {
            var block = ModBlocks.TWINNED_EFFIGY;

            var unlit = BlockModelGenerators.plainVariant(COLUMN.create(block, generators.modelOutput));
            var lit = BlockModelGenerators.plainVariant(COLUMN.get(block)
                    .updateTextures(mapping -> mapping.put(SIDE, TextureMapping.getBlockTexture(block, "_side_on")))
                    .createWithSuffix(block, "_on", generators.modelOutput));

            generators.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(block)
                            .with(BlockModelGenerators.createBooleanModelDispatch(LIT, lit, unlit)));
        }

        @Override
        public void generateItemModels(ItemModelGenerators generators) {
            var block = ModBlocks.TWINNED_EFFIGY;
            var item = ModItems.TWINNED_EFFIGY;

            generators.generateBooleanDispatch(item,
                    ItemModelUtils.hasComponent(DataComponents.PROFILE),
                    ItemModelUtils.plainModel(CUBE_TOP.getDefaultModelLocation(block).withSuffix("_on")),
                    ItemModelUtils.plainModel(CUBE_TOP.getDefaultModelLocation(block)));
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
            builder.add(TwinnedEffigyItem.BOUND_NAME, nameOfTwinnedEffigy + " of %s");
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
                                    .apply(CopyComponentsFunction
                                            .copyComponentsFromBlockEntity(
                                                    BlockEntityTarget.BLOCK_ENTITY.contextParam())
                                            .include(DataComponents.LOCK)
                                            .include(DataComponents.PROFILE))
                                    .otherwise(LootItem.lootTableItem(ModItems.TWINNED_EFFIGY))))
                    .setRandomSequence(BuiltInRegistries.BLOCK.getKey(ModBlocks.TWINNED_EFFIGY).withPrefix("blocks/")));
        }

    }

    private static final class ModRecipeProvider extends FabricRecipeProvider {

        public ModRecipeProvider(FabricPackOutput output, CompletableFuture<Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public String getName() {
            return "Recipe";
        }

        @Override
        protected RecipeProvider createRecipeProvider(Provider registries, RecipeOutput output) {
            return new RecipeProvider(registries, output) {
                @Override
                public void buildRecipes() {
                    shaped(RecipeCategory.MISC, ModItems.TWINNED_EFFIGY)
                            .pattern("###")
                            .pattern("#@#")
                            .pattern("###")
                            .define('#', Items.CHISELED_STONE_BRICKS)
                            .define('@', Items.ENDER_EYE)
                            .unlockedBy(getHasName(Items.CHISELED_STONE_BRICKS), has(Items.CHISELED_STONE_BRICKS))
                            .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                            .save(output);
                }
            };
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
