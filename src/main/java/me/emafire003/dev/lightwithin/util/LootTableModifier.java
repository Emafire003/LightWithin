package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.items.LightItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class LootTableModifier {
    private static final Identifier ANCIENT_CITY
            = new Identifier("minecraft", "chests/ancient_city");

    private static final Identifier MINESHAFT
            = new Identifier("minecraft", "chests/abandoned_mineshaft");

    private static final Identifier PILLAGER_OUTPOST
            = new Identifier("minecraft", "chests/pillager_outpost");

    private static final Identifier SIMPLE_DUNGEON
            = new Identifier("minecraft", "chests/simple_dungeon");

    private static final Identifier SHIPWRECK_MAP
            = new Identifier("minecraft", "chests/shipwreck_map");

    private static final Identifier STRONGHOLD_LIBRARY
            = new Identifier("minecraft", "chests/stronghold_library");

    private static final Identifier END_CITY_TREASURE
            = new Identifier("minecraft", "chests/end_city_treasure");

    private static final Identifier IGLOO_STRUCTURE_CHEST_ID
            = new Identifier("minecraft", "chests/igloo_chest");



    public static void modifyLootTables() {
        float luxcognita_chance = 0.23f;
        float luxintus_chance = 0.1f;
        float luxmutua_chance = 0.02f;
        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if (IGLOO_STRUCTURE_CHEST_ID.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxcognita_chance))
                        .with(ItemEntry.builder(LightItems.LUXCOGNITA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                supplier.pool(poolBuilder.build());

                LootPool.Builder poolBuilder1 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxintus_chance))
                        .with(ItemEntry.builder(LightItems.LUXINTUS_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder1.build());

                LootPool.Builder poolBuilder2 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxmutua_chance))
                        .with(ItemEntry.builder(LightItems.LUXMUTUA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder2.build());
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if (MINESHAFT.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxcognita_chance))
                        .with(ItemEntry.builder(LightItems.LUXCOGNITA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                supplier.pool(poolBuilder.build());

                LootPool.Builder poolBuilder1 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxintus_chance))
                        .with(ItemEntry.builder(LightItems.LUXINTUS_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                supplier.pool(poolBuilder1.build());

                LootPool.Builder poolBuilder2 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxmutua_chance))
                        .with(ItemEntry.builder(LightItems.LUXMUTUA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder2.build());
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if (SHIPWRECK_MAP.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxcognita_chance+0.05f))
                        .with(ItemEntry.builder(LightItems.LUXCOGNITA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder.build());

                LootPool.Builder poolBuilder1 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxintus_chance))
                        .with(ItemEntry.builder(LightItems.LUXINTUS_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder1.build());

                LootPool.Builder poolBuilder2 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxmutua_chance+0.01f))
                        .with(ItemEntry.builder(LightItems.LUXMUTUA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder2.build());
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if (SIMPLE_DUNGEON.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxcognita_chance))
                        .with(ItemEntry.builder(LightItems.LUXCOGNITA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                supplier.pool(poolBuilder.build());

                LootPool.Builder poolBuilder1 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxintus_chance))
                        .with(ItemEntry.builder(LightItems.LUXINTUS_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder1.build());

                LootPool.Builder poolBuilder2 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxmutua_chance))
                        .with(ItemEntry.builder(LightItems.LUXMUTUA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder2.build());
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if (PILLAGER_OUTPOST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxcognita_chance))
                        .with(ItemEntry.builder(LightItems.LUXCOGNITA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                supplier.pool(poolBuilder.build());

                LootPool.Builder poolBuilder1 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxintus_chance))
                        .with(ItemEntry.builder(LightItems.LUXINTUS_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder1.build());

            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if (STRONGHOLD_LIBRARY.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxcognita_chance+0.27f))
                        .with(ItemEntry.builder(LightItems.LUXCOGNITA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                supplier.pool(poolBuilder.build());

                LootPool.Builder poolBuilder1 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxintus_chance+0.05f))
                        .with(ItemEntry.builder(LightItems.LUXINTUS_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder1.build());

                LootPool.Builder poolBuilder2 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxmutua_chance))
                        .with(ItemEntry.builder(LightItems.LUXMUTUA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder2.build());
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if (END_CITY_TREASURE.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxcognita_chance+0.01f))
                        .with(ItemEntry.builder(LightItems.LUXCOGNITA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                supplier.pool(poolBuilder.build());

                LootPool.Builder poolBuilder1 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxintus_chance+0.01f))
                        .with(ItemEntry.builder(LightItems.LUXINTUS_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder1.build());

                LootPool.Builder poolBuilder2 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxmutua_chance+0.01f))
                        .with(ItemEntry.builder(LightItems.LUXMUTUA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder2.build());
            }
        }));

        LootTableEvents.MODIFY.register(((resourceManager, manager, id, supplier, setter) -> {
            if (ANCIENT_CITY.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxcognita_chance+0.12f))
                        .with(ItemEntry.builder(LightItems.LUXCOGNITA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                supplier.pool(poolBuilder.build());

                LootPool.Builder poolBuilder1 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxintus_chance+0.05f))
                        .with(ItemEntry.builder(LightItems.LUXINTUS_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder1.build());

                LootPool.Builder poolBuilder2 = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(luxmutua_chance+0.05f))
                        .with(ItemEntry.builder(LightItems.LUXMUTUA_BERRY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                supplier.pool(poolBuilder2.build());
            }
        }));
    }
}
