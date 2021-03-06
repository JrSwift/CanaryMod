package net.minecraft.entity.player;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.canarymod.Canary;
import net.canarymod.api.CanaryNetServerHandler;
import net.canarymod.api.PlayerListEntry;
import net.canarymod.api.chat.CanaryChatComponent;
import net.canarymod.api.entity.living.animal.EntityAnimal;
import net.canarymod.api.entity.living.humanoid.CanaryPlayer;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.entity.vehicle.CanaryChestMinecart;
import net.canarymod.api.inventory.CanaryAnimalInventory;
import net.canarymod.api.inventory.CanaryEnderChestInventory;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.NativeCustomStorageInventory;
import net.canarymod.api.nbt.CompoundTag;
import net.canarymod.api.packet.CanaryPacket;
import net.canarymod.api.statistics.CanaryStat;
import net.canarymod.api.world.CanaryWorld;
import net.canarymod.api.world.blocks.CanaryAnvil;
import net.canarymod.api.world.blocks.CanaryDoubleChest;
import net.canarymod.api.world.blocks.CanaryEnchantmentTable;
import net.canarymod.api.world.blocks.CanaryWorkbench;
import net.canarymod.api.world.position.Location;
import net.canarymod.config.Configuration;
import net.canarymod.config.WorldConfiguration;
import net.canarymod.hook.CancelableHook;
import net.canarymod.hook.entity.DimensionSwitchHook;
import net.canarymod.hook.player.*;
import net.minecraft.command.ICommand;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.*;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {

    private static final Logger bL = LogManager.getLogger();
    public String bM = "en_US"; // CanaryMod: private -> public
    public NetHandlerPlayServer a;
    public final MinecraftServer b;
    public final ItemInWorldManager c;
    public double d;
    public double e;
    public final List f = new LinkedList();
    public final List bN = new LinkedList();
    private final StatisticsFile bO;
    private float bP = Float.MIN_VALUE;
    private float bQ = -1.0E8F;
    private int bR = -99999999;
    private boolean bS = true;
    private int bT = -99999999;
    private int bU = 60;
    private EntityPlayer.EnumChatVisibility bV;
    private boolean bW = true;
    private long bX = System.currentTimeMillis();
    private int bY;
    public boolean g;
    public int h;
    public boolean i;

    public EntityPlayerMP(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, ItemInWorldManager iteminworldmanager) {
        super(worldserver, gameprofile);
        WorldConfiguration cfg = Configuration.getWorldConfig(worldserver.getCanaryWorld().getFqName());
        iteminworldmanager.b = this;
        this.c = iteminworldmanager;
        ChunkCoordinates chunkcoordinates = worldserver.K();
        int i0 = chunkcoordinates.a;
        int i1 = chunkcoordinates.c;
        int i2 = chunkcoordinates.b;

        if (!worldserver.t.g && worldserver.N().r() != WorldSettings.GameType.ADVENTURE) {
            int i3 = Math.max(5, cfg.getSpawnProtectionSize() - 6);

            i0 += this.Z.nextInt(i3 * 2) - i3;
            i1 += this.Z.nextInt(i3 * 2) - i3;
            i2 = worldserver.i(i0, i1);
        }

        this.b = minecraftserver;
        this.bO = minecraftserver.ah().a((EntityPlayer) this);
        this.W = 0.0F;
        this.L = 0.0F;
        this.b((double) i0 + 0.5D, (double) i2, (double) i1 + 0.5D, 0.0F, 0.0F);

        while (!worldserver.a(this, this.C).isEmpty()) {
            this.b(this.s, this.t + 1.0D, this.u);
        }

        this.entity = new CanaryPlayer(this); // CanaryMod: wrap entity

    }

    /* Special Constructor to keep a wrapper reference intact */
    public EntityPlayerMP(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, ItemInWorldManager iteminworldmanager, CanaryPlayer canaryPlayer) {
        super(worldserver, gameprofile);
        WorldConfiguration cfg = Configuration.getWorldConfig(worldserver.getCanaryWorld().getFqName());
        iteminworldmanager.b = this;
        this.c = iteminworldmanager;
        ChunkCoordinates chunkcoordinates = worldserver.K();
        int i0 = chunkcoordinates.a;
        int i1 = chunkcoordinates.c;
        int i2 = chunkcoordinates.b;

        if (!worldserver.t.g && worldserver.N().r() != WorldSettings.GameType.ADVENTURE) {
            int i3 = Math.max(5, cfg.getSpawnProtectionSize() - 6);

            i0 += this.Z.nextInt(i3 * 2) - i3;
            i1 += this.Z.nextInt(i3 * 2) - i3;
            i2 = worldserver.i(i0, i1);
        }

        this.b = minecraftserver;
        this.bO = minecraftserver.ah().a((EntityPlayer) this);
        this.W = 0.0F;
        this.L = 0.0F;
        this.b((double) i0 + 0.5D, (double) i2, (double) i1 + 0.5D, 0.0F, 0.0F);

        while (!worldserver.a(this, this.C).isEmpty()) {
            this.b(this.s, this.t + 1.0D, this.u);
        }

        // Why? To reduce load on data access initializing a new Player wrapper,
        // to reduce possible memory leaking on old EntityPlayerMP references
        Canary.log.debug("Keeping CanaryPlayer wrapper intact");
        this.entity = canaryPlayer;
        canaryPlayer.resetNativeEntityReference(this);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.b("playerGameType", 99)) {
            if (MinecraftServer.I().ap()) {
                this.c.a(MinecraftServer.I().i());
            } else {
                this.c.a(WorldSettings.GameType.a(nbttagcompound.f("playerGameType")));
            }
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.a("playerGameType", this.c.b().a());
    }

    public void a(int i0) {
        super.a(i0);
        this.bT = -1;
    }

    public void d_() {
        this.bo.a((ICrafting) this);
    }

    protected void e_() {
        this.L = 0.0F;
    }

    public float g() {
        return 1.62F;
    }

    public void h() {
        this.c.a();
        --this.bU;
        if (this.ad > 0) {
            --this.ad;
        }
        this.bo.b();
        if (!this.o.E && !this.bo.a((EntityPlayer) this)) {
            this.k();
            this.bo = this.bn;
        }

        while (!this.bN.isEmpty()) {
            int i0 = Math.min(this.bN.size(), 127);
            int[] aint = new int[i0];
            Iterator iterator = this.bN.iterator();
            int i1 = 0;

            while (iterator.hasNext() && i1 < i0) {
                aint[i1++] = ((Integer) iterator.next()).intValue();
                iterator.remove();
            }

            this.a.a((Packet) (new S13PacketDestroyEntities(aint)));
        }

        if (!this.f.isEmpty()) {
            ArrayList arraylist = new ArrayList();
            Iterator iterator1 = this.f.iterator();
            ArrayList arraylist1 = new ArrayList();

            Chunk chunk;

            while (iterator1.hasNext() && arraylist.size() < S26PacketMapChunkBulk.c()) {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator1.next();

                if (chunkcoordintpair != null) {
                    if (this.o.d(chunkcoordintpair.a << 4, 0, chunkcoordintpair.b << 4)) {
                        chunk = this.o.e(chunkcoordintpair.a, chunkcoordintpair.b);
                        if (chunk.k()) {
                            arraylist.add(chunk);
                            arraylist1.addAll(((WorldServer) this.o).a(chunkcoordintpair.a * 16, 0, chunkcoordintpair.b * 16, chunkcoordintpair.a * 16 + 16, 256, chunkcoordintpair.b * 16 + 16));
                            iterator1.remove();
                        }
                    }
                } else {
                    iterator1.remove();
                }
            }

            if (!arraylist.isEmpty()) {
                this.a.a((Packet) (new S26PacketMapChunkBulk(arraylist)));
                Iterator iterator2 = arraylist1.iterator();

                while (iterator2.hasNext()) {
                    TileEntity tileentity = (TileEntity) iterator2.next();

                    this.b(tileentity);
                }

                iterator2 = arraylist.iterator();

                while (iterator2.hasNext()) {
                    chunk = (Chunk) iterator2.next();
                    this.r().r().a(this, chunk);
                }
            }
        }
    }

    public void i() {
        try {
            super.h();

            for (int i0 = 0; i0 < this.bm.a(); ++i0) {
                ItemStack itemstack = this.bm.a(i0);

                if (itemstack != null && itemstack.b().h()) {
                    Packet packet = ((ItemMapBase) itemstack.b()).c(itemstack, this.o, this);

                    if (packet != null) {
                        this.a.a(packet);
                    }
                }
            }

            // CanaryMod: HealthChange / HealthEnabled
            if (this.aS() != this.bQ && bR != this.bp.a() && this.getPlayer() != null) {
                // updates your health when it is changed.
                if (!Configuration.getWorldConfig(getCanaryWorld().getFqName()).isHealthEnabled()) {
                    super.b(this.aZ());
                    this.K = false;
                } else {
                    HealthChangeHook hook = (HealthChangeHook) new HealthChangeHook(getPlayer(), bP, this.aN()).call();
                    if (hook.isCanceled()) {
                        super.b(this.bP);
                    }
                }
            }
            //

            if (this.aS() != this.bQ || this.bR != this.bp.a() || this.bp.e() == 0.0F != this.bS) {
                this.a.a((Packet) (new S06PacketUpdateHealth(this.aS(), this.bp.a(), this.bp.e())));
                this.bQ = this.aS();
                this.bR = this.bp.a();
                this.bS = this.bp.e() == 0.0F;
            }

            if (this.aS() + this.bs() != this.bP) {
                this.bP = this.aS() + this.bs();
                Collection collection = this.bU().a(IScoreObjectiveCriteria.f);
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    ScoreObjective scoreobjective = (ScoreObjective) iterator.next();

                    this.bU().a(this.b_(), scoreobjective).a(Arrays.asList(new EntityPlayer[]{this}));
                }
            }

            // CanaryMod: ExperienceHook / ExperienceEnabled
            if (!Configuration.getWorldConfig(getCanaryWorld().getFqName()).isExperienceEnabled()) {
                this.bT = 0;
                this.bG = 0;
            } else if (this.bG != this.bT) {
                ExperienceHook hook = (ExperienceHook) new ExperienceHook(getPlayer(), this.bT, this.bG).call();

                if (!hook.isCanceled()) {
                    this.bT = this.bG;
                    this.a.a(new S1FPacketSetExperience(this.bH, this.bG, this.bF));
                }
            }
            //

            if (this.aa % 20 * 5 == 0 && !this.w().a(AchievementList.L)) {
                this.j();
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Ticking player");
            CrashReportCategory crashreportcategory = crashreport.a("Player being ticked");

            this.a(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    protected void j() {
        BiomeGenBase biomegenbase = this.o.a(MathHelper.c(this.s), MathHelper.c(this.u));

        if (biomegenbase != null) {
            String s0 = biomegenbase.af;
            JsonSerializableSet jsonserializableset = (JsonSerializableSet) this.w().b((StatBase) AchievementList.L);

            if (jsonserializableset == null) {
                jsonserializableset = (JsonSerializableSet) this.w().a(AchievementList.L, new JsonSerializableSet());
            }
            jsonserializableset.add(s0);
            if (this.w().b(AchievementList.L) && jsonserializableset.size() == BiomeGenBase.n.size()) {
                HashSet hashset = Sets.newHashSet(BiomeGenBase.n);
                Iterator iterator = jsonserializableset.iterator();

                while (iterator.hasNext()) {
                    String s1 = (String) iterator.next();
                    Iterator iterator1 = hashset.iterator();

                    while (iterator1.hasNext()) {
                        BiomeGenBase biomegenbase1 = (BiomeGenBase) iterator1.next();

                        if (biomegenbase1.af.equals(s1)) {
                            iterator1.remove();
                        }
                    }

                    if (hashset.isEmpty()) {
                        break;
                    }
                }

                if (hashset.isEmpty()) {
                    this.a((StatBase) AchievementList.L);
                }
            }
        }

    }

    public void a(DamageSource damagesource) {
        // CanaryMod: Start: PlayerDeathHook
        PlayerDeathHook hook = (PlayerDeathHook) new PlayerDeathHook(getPlayer(), damagesource.getCanaryDamageSource(), this.aW().b().getWrapper()).call();
        // Check Death Message enabled
        if (Configuration.getServerConfig().isDeathMessageEnabled()) {
            this.b.ah().a(((CanaryChatComponent) hook.getDeathMessage1()).getNative());
        }
        //this.b.af().a(this.aW().b()); // Replaced Above
        // CanaryMod: End: PlayerDeathHook

        if (!this.o.O().b("keepInventory")) {
            this.bm.m();
        }

        Collection collection = this.o.W().a(IScoreObjectiveCriteria.c);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
            Score score = this.bU().a(this.b_(), scoreobjective);

            score.a();
        }

        EntityLivingBase entitylivingbase = this.aX();

        if (entitylivingbase != null) {
            int i0 = EntityList.a(entitylivingbase);
            EntityList.EntityEggInfo entitylist_entityegginfo = (EntityList.EntityEggInfo) EntityList.a.get(Integer.valueOf(i0));

            if (entitylist_entityegginfo != null) {
                this.a(entitylist_entityegginfo.e, 1);
            }
            entitylivingbase.b(this, this.ba);
        }

        this.a(StatList.v, 1);
    }

    public boolean a(DamageSource damagesource, float f0) {
        if (this.aw()) {
            return false;
        } else {
            // CanaryMod moved pvp to per-world config
            boolean haspvp = Configuration.getWorldConfig(getCanaryWorld().getFqName()).isPvpEnabled();
            boolean flag0 = this.b.X() && haspvp && "fall".equals(damagesource.o);

            if (!flag0 && this.bU > 0 && damagesource != DamageSource.i) {
                return false;
            } else {
                if (damagesource instanceof EntityDamageSource) {
                    Entity entity = damagesource.j();

                    if (entity instanceof EntityPlayer && !this.a((EntityPlayer) entity)) {
                        return false;
                    }

                    if (entity instanceof EntityArrow) {
                        EntityArrow entityarrow = (EntityArrow) entity;

                        if (entityarrow.c instanceof EntityPlayer && !this.a((EntityPlayer) entityarrow.c)) {
                            return false;
                        }
                    }
                }

                return super.a(damagesource, f0);
            }
        }
    }

    public boolean a(EntityPlayer entityplayer) {
        // CanaryMod moved pvp to per-world config
        boolean haspvp = Configuration.getWorldConfig(getCanaryWorld().getFqName()).isPvpEnabled();
        return haspvp && super.a(entityplayer);
    }

    public void b(int i0) {
        if (this.ap == 1 && i0 == 1) {
            this.a((StatBase) AchievementList.D);
            this.o.e((Entity) this);
            this.i = true;
            this.a.a((Packet) (new S2BPacketChangeGameState(4, 0.0F)));
        } else {
            if (this.ap == 0 && i0 == 1) {
                this.a((StatBase) AchievementList.C);
                ChunkCoordinates chunkcoordinates = this.b.getWorld(this.getCanaryWorld().getName(), i0).l();

                if (chunkcoordinates != null) {
                    // CanaryMod: Teleport Cause added
                    this.a.a((double) chunkcoordinates.a, (double) chunkcoordinates.b, (double) chunkcoordinates.c, 0.0F, 0.0F, getCanaryWorld().getType().getId(), getCanaryWorld().getName(), TeleportHook.TeleportCause.PORTAL);
                }

                i0 = 1;
            } else {
                this.a((StatBase) AchievementList.y);
            }

            // CanaryMod onPortalUse && onDimensionSwitch
            Location goingTo = simulatePortalUse(i0, MinecraftServer.I().getWorld(getCanaryWorld().getName(), i0));

            PortalUseHook puh = (PortalUseHook) new PortalUseHook(getPlayer(), goingTo).call();
            DimensionSwitchHook dsh = (DimensionSwitchHook) new DimensionSwitchHook(this.getCanaryEntity(), this.getCanaryEntity().getLocation(), goingTo).call();

            if (puh.isCanceled() || dsh.isCanceled()) {
                return;
            } //
            else {
                this.b.ah().a(this, getCanaryWorld().getName(), i0);
                this.bT = -1;
                this.bQ = -1.0F;
                this.bR = -1;
            } //
        }
    }

    private void b(TileEntity tileentity) {
        if (tileentity != null) {
            // CanaryMod: SignShowHook
            if (tileentity instanceof TileEntitySign) {
                new SignShowHook(this.getPlayer(), ((TileEntitySign) tileentity).getCanarySign()).call();
            }
            //
            Packet packet = tileentity.m();

            if (packet != null) {
                this.a.a(packet);
            }
        }
    }

    public void a(Entity entity, int i0) {
        super.a(entity, i0);
        this.bo.b();
    }

    public EnumStatus a(int i0, int i1, int i2) {
        EnumStatus entityplayer_enumstatus = super.a(i0, i1, i2);

        if (entityplayer_enumstatus == EnumStatus.OK) {
            S0APacketUseBed s0apacketusebed = new S0APacketUseBed(this, i0, i1, i2);

            this.r().r().a((Entity) this, (Packet) s0apacketusebed);
            // CanaryMod: Teleport Cause added
            this.a.a(this.s, this.t, this.u, this.y, this.z, getCanaryWorld().getType().getId(), getCanaryWorld().getName(), TeleportHook.TeleportCause.BED);
            this.a.a((Packet) s0apacketusebed);
        }

        return entityplayer_enumstatus;
    }

    public void a(boolean flag0, boolean flag1, boolean flag2) {
        if (this.bm()) {
            this.r().r().b(this, new S0BPacketAnimation(this, 2));
        }

        super.a(flag0, flag1, flag2);
        if (this.a != null) {
            // CanaryMod: Teleport Cause added
            this.a.a(this.s, this.t, this.u, this.y, this.z, getCanaryWorld().getType().getId(), getCanaryWorld().getName(), TeleportHook.TeleportCause.BED);
        }
    }

    public void a(Entity entity) {
        super.a(entity);
        this.a.a((Packet) (new S1BPacketEntityAttach(0, this, this.m)));
        // CanaryMod: Teleport Cause added
        this.a.a(this.s, this.t, this.u, this.y, this.z, getCanaryWorld().getType().getId(), getCanaryWorld().getName(), TeleportHook.TeleportCause.MOUNT_CHANGE);
    }

    protected void a(double d0, boolean flag0) {
    }

    public void b(double d0, boolean flag0) {
        super.a(d0, flag0);
    }

    public void a(TileEntity tileentity) {
        if (tileentity instanceof TileEntitySign) {
            ((TileEntitySign) tileentity).a((EntityPlayer) this);
            this.a.a((Packet) (new S36PacketSignEditorOpen(tileentity.c, tileentity.d, tileentity.e)));
        }
    }

    private void bV() {
        this.bY = this.bY % 100 + 1;
    }

    public void b(int i0, int i1, int i2) {
        // CanaryMod: InventoryHook
        ContainerWorkbench container = new ContainerWorkbench(this.bm, this.o, i0, i1, i2);
        CanaryWorkbench bench = (CanaryWorkbench) container.getInventory();
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), bench, false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 1, "Crafting", 9, true)));
        this.bo = container;
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(int i0, int i1, int i2, String s0) {
        // CanaryMod: InventoryHook
        ContainerEnchantment container = new ContainerEnchantment(this.bm, this.o, i0, i1, i2);
        CanaryEnchantmentTable table = (CanaryEnchantmentTable) container.getInventory();
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), table, false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 4, s0 == null ? "" : s0, 9, s0 != null)));
        this.bo = container;
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void c(int i0, int i1, int i2) {
        // CanaryMod: InventoryHook
        ContainerRepair container = new ContainerRepair(this.bm, this.o, i0, i1, i2, this);
        CanaryAnvil anvil = (CanaryAnvil) container.getInventory();
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), anvil, false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 8, "Repairing", 9, true)));
        this.bo = container;
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(IInventory iinventory) { // Open Inventory
        if (this.bo != this.bn) {
            this.k();
        }
        // CanaryMod: InventoryHook
        Inventory inventory = null;
        ContainerChest container = new ContainerChest(this.bm, iinventory);

        if (iinventory instanceof TileEntityChest) {
            inventory = ((TileEntityChest) iinventory).getCanaryChest();
            container.setInventory(inventory);
        } else if (iinventory instanceof InventoryLargeChest) {
            inventory = new CanaryDoubleChest((InventoryLargeChest) iinventory);
            container.setInventory(inventory);
        } else if (iinventory instanceof InventoryEnderChest) {
            inventory = new CanaryEnderChestInventory((InventoryEnderChest) iinventory, getPlayer());
            container.setInventory(inventory);
        } else if (iinventory instanceof EntityMinecartChest) {
            inventory = (CanaryChestMinecart) ((EntityMinecartChest) iinventory).getCanaryEntity();
            container.setInventory(inventory);
        } else if (iinventory instanceof NativeCustomStorageInventory) {
            inventory = ((NativeCustomStorageInventory) iinventory).getCanaryCustomInventory();
            container.setInventory(inventory);
        }

        if (inventory != null) {
            InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), inventory, false).call();
            if (hook.isCanceled()) {
                return;
            }
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 0, iinventory.b(), iinventory.a(), iinventory.k_())));
        this.bo = container;
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(TileEntityHopper tileentityhopper) {
        // CanaryMod: InventoryHook
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), tileentityhopper.getCanaryHopper(), false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 9, tileentityhopper.b(), tileentityhopper.a(), tileentityhopper.k_())));
        this.bo = new ContainerHopper(this.bm, tileentityhopper);
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(EntityMinecartHopper entityminecarthopper) {
        // CanaryMod: InventoryHook
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), (Inventory) entityminecarthopper.getCanaryEntity(), false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 9, entityminecarthopper.b(), entityminecarthopper.a(), entityminecarthopper.k_())));
        this.bo = new ContainerHopper(this.bm, entityminecarthopper);
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(TileEntityFurnace tileentityfurnace) {
        // CanaryMod: InventoryHook
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), tileentityfurnace.getCanaryFurnace(), false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 2, tileentityfurnace.b(), tileentityfurnace.a(), tileentityfurnace.k_())));
        this.bo = new ContainerFurnace(this.bm, tileentityfurnace);
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(TileEntityDispenser tileentitydispenser) {
        // CanaryMod: InventoryHook
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), tileentitydispenser.getCanaryDispenser(), false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, tileentitydispenser instanceof TileEntityDropper ? 10 : 3, tileentitydispenser.b(), tileentitydispenser.a(), tileentitydispenser.k_())));
        this.bo = new ContainerDispenser(this.bm, tileentitydispenser);
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(TileEntityBrewingStand tileentitybrewingstand) {
        // CanaryMod: InventoryHook
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), tileentitybrewingstand.getCanaryBrewingStand(), false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 5, tileentitybrewingstand.b(), tileentitybrewingstand.a(), tileentitybrewingstand.k_())));
        this.bo = new ContainerBrewingStand(this.bm, tileentitybrewingstand);
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(TileEntityBeacon tileentitybeacon) {
        // CanaryMod: InventoryHook
        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), tileentitybeacon.getCanaryBeacon(), false).call();
        if (hook.isCanceled()) {
            return;
        }
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 7, tileentitybeacon.b(), tileentitybeacon.a(), tileentitybeacon.k_())));
        this.bo = new ContainerBeacon(this.bm, tileentitybeacon);
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(IMerchant imerchant, String s0) {
        this.bV();
        this.bo = new ContainerMerchant(this.bm, imerchant, this.o);
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
        InventoryMerchant inventorymerchant = ((ContainerMerchant) this.bo).e();

        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 6, s0 == null ? "" : s0, inventorymerchant.a(), s0 != null)));
        MerchantRecipeList merchantrecipelist = imerchant.b(this);

        if (merchantrecipelist != null) {
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());

            try {
                packetbuffer.writeInt(this.bY);
                merchantrecipelist.a(packetbuffer);
                this.a.a((Packet) (new S3FPacketCustomPayload("MC|TrList", packetbuffer)));
            } catch (IOException ioexception) {
                bL.error("Couldn\'t send trade list", ioexception);
            } finally {
                packetbuffer.release();
            }
        }
    }

    public void a(EntityHorse entityhorse, IInventory iinventory) {
        if (this.bo != this.bn) {
            this.k();
        }
        // CanaryMod: InventoryHook
        Inventory inv = new CanaryAnimalInventory((AnimalChest) iinventory, (EntityAnimal) entityhorse.getCanaryEntity());

        InventoryHook hook = (InventoryHook) new InventoryHook(getPlayer(), inv, false).call();
        if (hook.isCanceled()) {
            return;
        }
        ContainerHorseInventory chi = new ContainerHorseInventory(this.bm, iinventory, entityhorse);
        chi.setInventory(inv);
        //
        this.bV();
        this.a.a((Packet) (new S2DPacketOpenWindow(this.bY, 11, iinventory.b(), iinventory.a(), iinventory.k_(), entityhorse.y())));
        this.bo = chi;
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void a(Container container, int i0, ItemStack itemstack) {
        if (!(container.a(i0) instanceof SlotCrafting)) {
            if (!this.g) {
                this.a.a((Packet) (new S2FPacketSetSlot(container.d, i0, itemstack)));
            }
        }
    }

    public void a(Container container) {
        this.a(container, container.a());
    }

    public void a(Container container, List list) {
        this.a.a((Packet) (new S30PacketWindowItems(container.d, list)));
        this.a.a((Packet) (new S2FPacketSetSlot(-1, -1, this.bm.o())));
    }

    public void a(Container container, int i0, int i1) {
        this.a.a((Packet) (new S31PacketWindowProperty(container.d, i0, i1)));
    }

    public void k() {
        this.a.a((Packet) (new S2EPacketCloseWindow(this.bo.d)));
        this.m();
    }

    public void l() {
        if (!this.g) {
            this.a.a((Packet) (new S2FPacketSetSlot(-1, -1, this.bm.o())));
        }
    }

    public void m() {
        this.bo.b((EntityPlayer) this);
        this.bo = this.bn;
    }

    public void a(float f0, float f1, boolean flag0, boolean flag1) {
        if (this.m != null) {
            if (f0 >= -1.0F && f0 <= 1.0F) {
                this.bd = f0;
            }

            if (f1 >= -1.0F && f1 <= 1.0F) {
                this.be = f1;
            }

            this.bc = flag0;
            this.b(flag1);
        }
    }

    public void a(StatBase statbase, int i0) {
        if (statbase != null) {
            // CanaryMod: StatGained
            StatGainedHook hook = (StatGainedHook) new StatGainedHook(getPlayer(), new CanaryStat(statbase)).call();
            if (hook.isCanceled()) {
                return;
            }
            this.bO.b(this, statbase, i0);
            Iterator iterator = this.bU().a(statbase.k()).iterator();

            while (iterator.hasNext()) {
                ScoreObjective scoreobjective = (ScoreObjective) iterator.next();

                this.bU().a(this.b_(), scoreobjective).a();
            }

            if (this.bO.e()) {
                this.bO.a(this);
            }
        }
    }

    public void n() {
        if (this.l != null) {
            this.l.a((Entity) this);
        }

        if (this.bA) {
            this.a(true, false, false);
        }
    }

    public void o() {
        this.bQ = -1.0E8F;
    }

    public void b(IChatComponent ichatcomponent) {
        this.a.a((Packet) (new S02PacketChat(ichatcomponent)));
    }

    protected void p() {
        this.a.a((Packet) (new S19PacketEntityStatus(this, (byte) 9)));
        super.p();
    }

    public void a(ItemStack itemstack, int i0) {
        super.a(itemstack, i0);
        if (itemstack != null && itemstack.b() != null && itemstack.b().d(itemstack) == EnumAction.eat) {
            this.r().r().b(this, new S0BPacketAnimation(this, 3));
        }
    }

    public void a(EntityPlayer entityplayer, boolean flag0) {
        super.a(entityplayer, flag0);
        this.bT = -1;
        this.bQ = -1.0F;
        this.bR = -1;
        this.bN.addAll(((EntityPlayerMP) entityplayer).bN);
    }

    protected void a(PotionEffect potioneffect) {
        super.a(potioneffect);
        this.a.a((Packet) (new S1DPacketEntityEffect(this.y(), potioneffect)));
    }

    protected void a(PotionEffect potioneffect, boolean flag0) {
        super.a(potioneffect, flag0);
        this.a.a((Packet) (new S1DPacketEntityEffect(this.y(), potioneffect)));
    }

    protected void b(PotionEffect potioneffect) {
        super.b(potioneffect);
        this.a.a((Packet) (new S1EPacketRemoveEntityEffect(this.y(), potioneffect)));
    }

    public void a(double d0, double d1, double d2) {
        this.a.a(d0, d1, d2, this.y, this.z, getCanaryWorld().getType().getId(), getCanaryWorld().getName(), TeleportHook.TeleportCause.MOVEMENT);
    }

    public void b(Entity entity) {
        this.r().r().b(this, new S0BPacketAnimation(entity, 4));
    }

    public void c(Entity entity) {
        this.r().r().b(this, new S0BPacketAnimation(entity, 5));
    }

    public void q() {
        if (this.a != null) {
            this.a.a((Packet) (new S39PacketPlayerAbilities(this.bE)));
        }
    }

    public WorldServer r() {
        return (WorldServer) this.o;
    }

    public void a(WorldSettings.GameType worldsettings_gametype) {
        this.c.a(worldsettings_gametype);
        this.a.a((Packet) (new S2BPacketChangeGameState(3, (float) worldsettings_gametype.a())));
    }

    public void a(IChatComponent ichatcomponent) {
        this.a.a((Packet) (new S02PacketChat(ichatcomponent)));
    }

    public boolean a(int i0, String s0) {
        // CanaryMod: replace permission checking with ours
        // return "seed".equals(s0) && !this.b.V() ? true : (!"tell".equals(s0) && !"help".equals(s0) && !"me".equals(s0) ? this.b.af().e(this.bu) ? this.b.k() >= i0 : false) : true);
        if (s0.trim().isEmpty()) { // Purely checking for permission level
            return getPlayer().hasPermission("canary.world.commandblock");
        }
        if (s0.startsWith("/")) {
            s0 = s0.substring(1);
        }
        String[] args = s0.split(" ");
        if (Canary.commands().hasCommand(args[0])) {
            return Canary.commands().canUseCommand(getPlayer(), args[0]);
        }
        // Might be vanilla, so just assume
        ICommand icommand = (ICommand) MinecraftServer.I().J().a().get(args[0]);
        if (icommand == null) {
            return false;
        }
        return Canary.ops().isOpped(getPlayer().getName()) || getPlayer().hasPermission("canary.commands.vanilla.".concat(icommand.c()));
        //
    }

    public String s() {
        String s0 = this.a.a.b().toString();

        s0 = s0.substring(s0.indexOf("/") + 1);
        s0 = s0.substring(0, s0.indexOf(":"));
        return s0;
    }

    public void a(C15PacketClientSettings c15packetclientsettings) {
        this.bM = c15packetclientsettings.c();
        int i0 = 256 >> c15packetclientsettings.d();

        if (i0 > 3 && i0 < 20) {
            ;
        }

        this.bV = c15packetclientsettings.e();
        this.bW = c15packetclientsettings.f();
        if (this.b.N() && this.b.M().equals(this.b_())) {
            this.b.a(c15packetclientsettings.g(), (WorldServer) this.d()); // CanaryMod: signature takes WorldServer now
        }

        this.b(1, !c15packetclientsettings.h());
    }

    public EnumChatVisibility u() {
        return this.bV;
    }

    public void a(String s0) {
        this.a.a((Packet) (new S3FPacketCustomPayload("MC|RPack", s0.getBytes(Charsets.UTF_8))));
    }

    public ChunkCoordinates f_() {
        return new ChunkCoordinates(MathHelper.c(this.s), MathHelper.c(this.t + 0.5D), MathHelper.c(this.u));
    }

    public void v() {
        this.bX = MinecraftServer.ar();
    }

    public StatisticsFile w() {
        return this.bO;
    }

    public void d(Entity entity) {
        if (entity instanceof EntityPlayer) {
            this.a.a((Packet) (new S13PacketDestroyEntities(new int[]{entity.y()})));
        } else {
            this.bN.add(Integer.valueOf(entity.y()));
        }

    }

    public long x() {
        return this.bX;
    }

    // CanaryMod: Override
    @Override
    public GameProfile bJ() {
        if (!this.getDisplayName().equals(this.b_())) {
            // We need a new GameProfile to change the display name
            return new GameProfile(this.aB(), this.getDisplayName());
        }
        return super.i;
    }

    @Override
    public void setDisplayName(String name) {
        PlayerListEntry entryOld = getPlayer().getPlayerListEntry(false);
        PlayerListEntry entryNew = entryOld.clone();
        entryNew.setName(name);
        entryNew.setShown(true);
        super.setDisplayName(name); // Change name
        for (Player player : Canary.getServer().getPlayerList()) {
            if (!player.equals(getPlayer())) {
                player.sendPacket(new CanaryPacket(new S0CPacketSpawnPlayer(this)));
            }
        }
        Canary.getServer().sendPlayerListEntry(entryNew);
        Canary.getServer().sendPlayerListEntry(entryOld);
    }

    public void updateSlot(int windowId, int slotIndex, ItemStack item) {
        this.a.a(new S2FPacketSetSlot(windowId, slotIndex, item));
    }

    public boolean getColorEnabled() {
        return this.bW;
    }

    public int getViewDistance() {
        return this.bU;
    }

    /**
     * Get the CanaryEntity as CanaryPlayer
     *
     * @return
     */
    public CanaryPlayer getPlayer() {
        return (CanaryPlayer) this.entity;
    }

    public CanaryNetServerHandler getServerHandler() {
        return a.getCanaryServerHandler();
    }

    public void setDimension(CanaryWorld world) {
        super.a(world.getHandle());
        this.c.a((WorldServer) world.getHandle());
    }

    public void changeWorld(WorldServer srv) {
        ChunkCoordinates chunkcoordinates = srv.l();

        if (chunkcoordinates != null) {
            this.a.a((double) chunkcoordinates.a, (double) chunkcoordinates.b, (double) chunkcoordinates.c, 0.0F, 0.0F, srv.getCanaryWorld().getType().getId(), srv.getCanaryWorld().getName(), TeleportHook.TeleportCause.PLUGIN);
        }

        // CanaryMod: Dimension switch hook.
        Location goingTo = this.simulatePortalUse(srv.q, MinecraftServer.I().getWorld(this.getCanaryWorld().getName(), srv.q));
        CancelableHook hook = (CancelableHook) new DimensionSwitchHook(this.getCanaryEntity(), this.getCanaryEntity().getLocation(), goingTo).call();
        if (hook.isCanceled()) {
            return;
        }//

        this.b.ah().a(this, srv.getCanaryWorld().getName(), srv.getCanaryWorld().getType().getId());
        this.bT = -1;
        this.bQ = -1.0F;
        this.bR = -1;
    }

    // CanaryMod: Special methods for remote inventory opening
    public void openContainer(Container container, int containerId, int size) {
        this.bV();
        this.a.a(new S2DPacketOpenWindow(this.bY, containerId, container.getInventory().getInventoryName(), size, true));
        this.bo = container;
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void openContainer(Container container, int containerId, int size, boolean flag) {
        this.bV();
        this.a.a(new S2DPacketOpenWindow(this.bY, containerId, container.getInventory().getInventoryName(), size, flag));
        this.bo = container;
        this.bo.d = this.bY;
        this.bo.a((ICrafting) this);
    }

    public void setMetaData(CompoundTag meta) {
        this.metadata = meta;
        this.b_();
    }

    public void saveMeta() {
        super.saveMeta();
        metadata.put("PreviousIP", getPlayer().getIP());
    }

    public String getLastJoined() {
        return metadata.getString("LastJoin");
    }

    public void storeLastJoin(String lastJoin) {
        metadata.put("LastJoin", lastJoin);
    }

    @Override
    public void initializeNewMeta() {
        if (metadata == null) {
            super.initializeNewMeta();
        }
    }
    //
}
