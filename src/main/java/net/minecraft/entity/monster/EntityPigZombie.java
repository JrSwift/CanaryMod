package net.minecraft.entity.monster;

import net.canarymod.api.entity.living.monster.CanaryPigZombie;
import net.canarymod.hook.entity.MobTargetHook;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class EntityPigZombie extends EntityZombie {

    private static final UUID bq = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier br = (new AttributeModifier(bq, "Attacking speed boost", 0.45D, 0)).a(false);
    public int bs; // CanaryMod: private -> public
    private int bt;
    private Entity bu;

    public EntityPigZombie(World world) {
        super(world);
        this.ae = true;
        this.entity = new CanaryPigZombie(this); // CanaryMod: Wrap Entity
    }

    protected void aD() {
        super.aD();
        this.a(bp).a(0.0D);
        this.a(SharedMonsterAttributes.d).a(0.5D);
        this.a(SharedMonsterAttributes.e).a(5.0D);
    }

    protected boolean bk() {
        return false;
    }

    public void h() {
        if (this.bu != this.bm && !this.o.E) {
            IAttributeInstance iattributeinstance = this.a(SharedMonsterAttributes.d);

            iattributeinstance.b(br);
            if (this.bm != null) {
                iattributeinstance.a(br);
            }
        }

        this.bu = this.bm;
        if (this.bt > 0 && --this.bt == 0) {
            this.a("mob.zombiepig.zpigangry", this.bf() * 2.0F, ((this.Z.nextFloat() - this.Z.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        super.h();
    }

    public boolean by() {
        return this.o.r != EnumDifficulty.PEACEFUL && this.o.b(this.C) && this.o.a((Entity) this, this.C).isEmpty() && !this.o.d(this.C);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.a("Anger", (short) this.bs);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.bs = nbttagcompound.e("Anger");
    }

    protected Entity bR() {
        return this.bs == 0 ? null : super.bR();
    }

    public boolean a(DamageSource damagesource, float f0) {
        if (this.aw()) {
            return false;
        } else {
            Entity entity = damagesource.j();

            if (entity instanceof EntityPlayer) {
                // CanaryMod: MobTarget
                MobTargetHook hook = (MobTargetHook) new MobTargetHook((net.canarymod.api.entity.living.LivingBase) this.getCanaryEntity(), (net.canarymod.api.entity.living.LivingBase) entity.getCanaryEntity()).call();
                if (!hook.isCanceled()) {

                    List list = this.o.b((Entity) this, this.C.b(32.0D, 32.0D, 32.0D));

                    for (int i0 = 0; i0 < list.size(); ++i0) {
                        Entity entity1 = (Entity) list.get(i0);

                        if (entity1 instanceof EntityPigZombie) {
                            EntityPigZombie entitypigzombie = (EntityPigZombie) entity1;
                            entitypigzombie.c(entity);
                        }
                    }
                    this.c(entity);
                }
                //
            }

            return super.a(damagesource, f0);
        }
    }

    public void c(Entity entity) { // CanaryMod: private => public; angryAt
        this.bm = entity;
        this.bs = 400 + this.Z.nextInt(400);
        this.bt = this.Z.nextInt(40);
    }

    protected String t() {
        return "mob.zombiepig.zpig";
    }

    protected String aT() {
        return "mob.zombiepig.zpighurt";
    }

    protected String aU() {
        return "mob.zombiepig.zpigdeath";
    }

    protected void b(boolean flag0, int i0) {
        int i1 = this.Z.nextInt(2 + i0);

        int i2;

        for (i2 = 0; i2 < i1; ++i2) {
            this.a(Items.bh, 1);
        }

        i1 = this.Z.nextInt(2 + i0);

        for (i2 = 0; i2 < i1; ++i2) {
            this.a(Items.bl, 1);
        }
    }

    public boolean a(EntityPlayer entityplayer) {
        return false;
    }

    protected void n(int i0) {
        this.a(Items.k, 1);
    }

    protected void bC() {
        this.c(0, new ItemStack(Items.B));
    }

    public IEntityLivingData a(IEntityLivingData ientitylivingdata) {
        super.a(ientitylivingdata);
        this.j(false);
        return ientitylivingdata;
    }
}
