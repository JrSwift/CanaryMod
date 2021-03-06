package net.minecraft.entity.boss;

import net.canarymod.api.entity.living.monster.CanaryEnderDragon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class EntityDragon extends EntityLiving implements IEntityMultiPart, IMob {

    public double h;
    public double i;
    public double bm;
    public double[][] bn = new double[64][3];
    public int bo = -1;
    public EntityDragonPart[] bp;
    public EntityDragonPart bq;
    public EntityDragonPart br;
    public EntityDragonPart bs;
    public EntityDragonPart bt;
    public EntityDragonPart bu;
    public EntityDragonPart bv;
    public EntityDragonPart bw;
    public float bx;
    public float by;
    public boolean bz;
    public boolean bA;
    private Entity bD;
    public int bB;
    public EntityEnderCrystal bC;

    public EntityDragon(World world) {
        super(world);
        this.bp = new EntityDragonPart[]{this.bq = new EntityDragonPart(this, "head", 6.0F, 6.0F), this.br = new EntityDragonPart(this, "body", 8.0F, 8.0F), this.bs = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.bt = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.bu = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.bv = new EntityDragonPart(this, "wing", 4.0F, 4.0F), this.bw = new EntityDragonPart(this, "wing", 4.0F, 4.0F)};
        this.g(this.aY());
        this.a(16.0F, 8.0F);
        this.X = true;
        this.ae = true;
        this.i = 100.0D;
        this.ak = true;
        this.entity = new CanaryEnderDragon(this); // CanaryMod: Wrap Entity
    }

    protected void aD() {
        super.aD();
        this.a(SharedMonsterAttributes.a).a(200.0D);
    }

    protected void c() {
        super.c();
    }

    public double[] b(int i0, float f0) {
        if (this.aS() <= 0.0F) {
            f0 = 0.0F;
        }

        f0 = 1.0F - f0;
        int i1 = this.bo - i0 * 1 & 63;
        int i2 = this.bo - i0 * 1 - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.bn[i1][0];
        double d1 = MathHelper.g(this.bn[i2][0] - d0);

        adouble[0] = d0 + d1 * (double) f0;
        d0 = this.bn[i1][1];
        d1 = this.bn[i2][1] - d0;
        adouble[1] = d0 + d1 * (double) f0;
        adouble[2] = this.bn[i1][2] + (this.bn[i2][2] - this.bn[i1][2]) * (double) f0;
        return adouble;
    }

    public void e() {
        float f0;
        float f1;

        if (this.o.E) {
            f0 = MathHelper.b(this.by * 3.1415927F * 2.0F);
            f1 = MathHelper.b(this.bx * 3.1415927F * 2.0F);
            if (f1 <= -0.3F && f0 >= -0.3F) {
                this.o.a(this.s, this.t, this.u, "mob.enderdragon.wings", 5.0F, 0.8F + this.Z.nextFloat() * 0.3F, false);
            }
        }

        this.bx = this.by;
        float f2;

        if (this.aS() <= 0.0F) {
            f0 = (this.Z.nextFloat() - 0.5F) * 8.0F;
            f1 = (this.Z.nextFloat() - 0.5F) * 4.0F;
            f2 = (this.Z.nextFloat() - 0.5F) * 8.0F;
            this.o.a("largeexplode", this.s + (double) f0, this.t + 2.0D + (double) f1, this.u + (double) f2, 0.0D, 0.0D, 0.0D);
        } else {
            this.bP();
            f0 = 0.2F / (MathHelper.a(this.v * this.v + this.x * this.x) * 10.0F + 1.0F);
            f0 *= (float) Math.pow(2.0D, this.w);
            if (this.bA) {
                this.by += f0 * 0.5F;
            } else {
                this.by += f0;
            }

            this.y = MathHelper.g(this.y);
            if (this.bo < 0) {
                for (int i0 = 0; i0 < this.bn.length; ++i0) {
                    this.bn[i0][0] = (double) this.y;
                    this.bn[i0][1] = this.t;
                }
            }

            if (++this.bo == this.bn.length) {
                this.bo = 0;
            }

            this.bn[this.bo][0] = (double) this.y;
            this.bn[this.bo][1] = this.t;
            double d0;
            double d1;
            double d2;
            double d3;
            float f3;

            if (this.o.E) {
                if (this.bg > 0) {
                    d3 = this.s + (this.bh - this.s) / (double) this.bg;
                    d0 = this.t + (this.bi - this.t) / (double) this.bg;
                    d1 = this.u + (this.bj - this.u) / (double) this.bg;
                    d2 = MathHelper.g(this.bk - (double) this.y);
                    this.y = (float) ((double) this.y + d2 / (double) this.bg);
                    this.z = (float) ((double) this.z + (this.bl - (double) this.z) / (double) this.bg);
                    --this.bg;
                    this.b(d3, d0, d1);
                    this.b(this.y, this.z);
                }
            } else {
                d3 = this.h - this.s;
                d0 = this.i - this.t;
                d1 = this.bm - this.u;
                d2 = d3 * d3 + d0 * d0 + d1 * d1;
                if (this.bD != null) {
                    this.h = this.bD.s;
                    this.bm = this.bD.u;
                    double d4 = this.h - this.s;
                    double d5 = this.bm - this.u;
                    double d6 = Math.sqrt(d4 * d4 + d5 * d5);
                    double d7 = 0.4000000059604645D + d6 / 80.0D - 1.0D;

                    if (d7 > 10.0D) {
                        d7 = 10.0D;
                    }

                    this.i = this.bD.C.b + d7;
                } else {
                    this.h += this.Z.nextGaussian() * 2.0D;
                    this.bm += this.Z.nextGaussian() * 2.0D;
                }

                if (this.bz || d2 < 100.0D || d2 > 22500.0D || this.E || this.F) {
                    this.bQ();
                }

                d0 /= (double) MathHelper.a(d3 * d3 + d1 * d1);
                f3 = 0.6F;
                if (d0 < (double) (-f3)) {
                    d0 = (double) (-f3);
                }

                if (d0 > (double) f3) {
                    d0 = (double) f3;
                }

                this.w += d0 * 0.10000000149011612D;
                this.y = MathHelper.g(this.y);
                double d8 = 180.0D - Math.atan2(d3, d1) * 180.0D / 3.1415927410125732D;
                double d9 = MathHelper.g(d8 - (double) this.y);

                if (d9 > 50.0D) {
                    d9 = 50.0D;
                }

                if (d9 < -50.0D) {
                    d9 = -50.0D;
                }

                Vec3 vec3 = Vec3.a(this.h - this.s, this.i - this.t, this.bm - this.u).a();
                Vec3 vec31 = Vec3.a((double) MathHelper.a(this.y * 3.1415927F / 180.0F), this.w, (double) (-MathHelper.b(this.y * 3.1415927F / 180.0F))).a();
                float f4 = (float) (vec31.b(vec3) + 0.5D) / 1.5F;

                if (f4 < 0.0F) {
                    f4 = 0.0F;
                }

                this.bf *= 0.8F;
                float f5 = MathHelper.a(this.v * this.v + this.x * this.x) * 1.0F + 1.0F;
                double d10 = Math.sqrt(this.v * this.v + this.x * this.x) * 1.0D + 1.0D;

                if (d10 > 40.0D) {
                    d10 = 40.0D;
                }

                this.bf = (float) ((double) this.bf + d9 * (0.699999988079071D / d10 / (double) f5));
                this.y += this.bf * 0.1F;
                float f6 = (float) (2.0D / (d10 + 1.0D));
                float f7 = 0.06F;

                this.a(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));
                if (this.bA) {
                    this.d(this.v * 0.800000011920929D, this.w * 0.800000011920929D, this.x * 0.800000011920929D);
                } else {
                    this.d(this.v, this.w, this.x);
                }

                Vec3 vec32 = Vec3.a(this.v, this.w, this.x).a();
                float f8 = (float) (vec32.b(vec31) + 1.0D) / 2.0F;

                f8 = 0.8F + 0.15F * f8;
                this.v *= (double) f8;
                this.x *= (double) f8;
                this.w *= 0.9100000262260437D;
            }

            this.aM = this.y;
            this.bq.M = this.bq.N = 3.0F;
            this.bs.M = this.bs.N = 2.0F;
            this.bt.M = this.bt.N = 2.0F;
            this.bu.M = this.bu.N = 2.0F;
            this.br.N = 3.0F;
            this.br.M = 5.0F;
            this.bv.N = 2.0F;
            this.bv.M = 4.0F;
            this.bw.N = 3.0F;
            this.bw.M = 4.0F;
            f1 = (float) (this.b(5, 1.0F)[1] - this.b(10, 1.0F)[1]) * 10.0F / 180.0F * 3.1415927F;
            f2 = MathHelper.b(f1);
            float f9 = -MathHelper.a(f1);
            float f10 = this.y * 3.1415927F / 180.0F;
            float f11 = MathHelper.a(f10);
            float f12 = MathHelper.b(f10);

            this.br.h();
            this.br.b(this.s + (double) (f11 * 0.5F), this.t, this.u - (double) (f12 * 0.5F), 0.0F, 0.0F);
            this.bv.h();
            this.bv.b(this.s + (double) (f12 * 4.5F), this.t + 2.0D, this.u + (double) (f11 * 4.5F), 0.0F, 0.0F);
            this.bw.h();
            this.bw.b(this.s - (double) (f12 * 4.5F), this.t + 2.0D, this.u - (double) (f11 * 4.5F), 0.0F, 0.0F);
            if (!this.o.E && this.ax == 0) {
                this.a(this.o.b((Entity) this, this.bv.C.b(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
                this.a(this.o.b((Entity) this, this.bw.C.b(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
                this.b(this.o.b((Entity) this, this.bq.C.b(1.0D, 1.0D, 1.0D)));
            }

            double[] adouble = this.b(5, 1.0F);
            double[] adouble1 = this.b(0, 1.0F);

            f3 = MathHelper.a(this.y * 3.1415927F / 180.0F - this.bf * 0.01F);
            float f13 = MathHelper.b(this.y * 3.1415927F / 180.0F - this.bf * 0.01F);

            this.bq.h();
            this.bq.b(this.s + (double) (f3 * 5.5F * f2), this.t + (adouble1[1] - adouble[1]) * 1.0D + (double) (f9 * 5.5F), this.u - (double) (f13 * 5.5F * f2), 0.0F, 0.0F);

            for (int i1 = 0; i1 < 3; ++i1) {
                EntityDragonPart entitydragonpart = null;

                if (i1 == 0) {
                    entitydragonpart = this.bs;
                }

                if (i1 == 1) {
                    entitydragonpart = this.bt;
                }

                if (i1 == 2) {
                    entitydragonpart = this.bu;
                }

                double[] adouble2 = this.b(12 + i1 * 2, 1.0F);
                float f14 = this.y * 3.1415927F / 180.0F + this.b(adouble2[0] - adouble[0]) * 3.1415927F / 180.0F * 1.0F;
                float f15 = MathHelper.a(f14);
                float f16 = MathHelper.b(f14);
                float f17 = 1.5F;
                float f18 = (float) (i1 + 1) * 2.0F;

                entitydragonpart.h();
                entitydragonpart.b(this.s - (double) ((f11 * f17 + f15 * f18) * f2), this.t + (adouble2[1] - adouble[1]) * 1.0D - (double) ((f18 + f17) * f9) + 1.5D, this.u + (double) ((f12 * f17 + f16 * f18) * f2), 0.0F, 0.0F);
            }

            if (!this.o.E) {
                this.bA = this.a(this.bq.C) | this.a(this.br.C);
            }
        }
    }

    private void bP() {
        if (this.bC != null) {
            if (this.bC.K) {
                if (!this.o.E) {
                    this.a(this.bq, DamageSource.a((Explosion) null), 10.0F);
                }

                this.bC = null;
            } else if (this.aa % 10 == 0 && this.aS() < this.aY()) {
                this.g(this.aS() + 1.0F);
            }
        }

        if (this.Z.nextInt(10) == 0) {
            float f0 = 32.0F;
            List list = this.o.a(EntityEnderCrystal.class, this.C.b((double) f0, (double) f0, (double) f0));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal) iterator.next();
                double d1 = entityendercrystal1.f(this);

                if (d1 < d0) {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }

            this.bC = entityendercrystal;
        }
    }

    private void a(List list) {
        double d0 = (this.br.C.a + this.br.C.d) / 2.0D;
        double d1 = (this.br.C.c + this.br.C.f) / 2.0D;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLivingBase) {
                double d2 = entity.s - d0;
                double d3 = entity.u - d1;
                double d4 = d2 * d2 + d3 * d3;

                entity.g(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
            }
        }
    }

    private void b(List list) {
        for (int i0 = 0; i0 < list.size(); ++i0) {
            Entity entity = (Entity) list.get(i0);

            if (entity instanceof EntityLivingBase) {
                entity.a(DamageSource.a((EntityLivingBase) this), 10.0F);
            }
        }
    }

    private void bQ() {
        this.bz = false;
        if (this.Z.nextInt(2) == 0 && !this.o.h.isEmpty()) {
            this.bD = (Entity) this.o.h.get(this.Z.nextInt(this.o.h.size()));
        } else {
            boolean flag0 = false;

            do {
                this.h = 0.0D;
                this.i = (double) (70.0F + this.Z.nextFloat() * 50.0F);
                this.bm = 0.0D;
                this.h += (double) (this.Z.nextFloat() * 120.0F - 60.0F);
                this.bm += (double) (this.Z.nextFloat() * 120.0F - 60.0F);
                double d0 = this.s - this.h;
                double d1 = this.t - this.i;
                double d2 = this.u - this.bm;

                flag0 = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
            }
            while (!flag0);

            this.bD = null;
        }
    }

    private float b(double d0) {
        return (float) MathHelper.g(d0);
    }

    private boolean a(AxisAlignedBB axisalignedbb) {
        int i0 = MathHelper.c(axisalignedbb.a);
        int i1 = MathHelper.c(axisalignedbb.b);
        int i2 = MathHelper.c(axisalignedbb.c);
        int i3 = MathHelper.c(axisalignedbb.d);
        int i4 = MathHelper.c(axisalignedbb.e);
        int i5 = MathHelper.c(axisalignedbb.f);
        boolean flag0 = false;
        boolean flag1 = false;

        for (int i6 = i0; i6 <= i3; ++i6) {
            for (int i7 = i1; i7 <= i4; ++i7) {
                for (int i8 = i2; i8 <= i5; ++i8) {
                    Block block = this.o.a(i6, i7, i8);

                    if (block.o() != Material.a) {
                        if (block != Blocks.Z && block != Blocks.bs && block != Blocks.h && this.o.O().b("mobGriefing")) {
                            flag1 = this.o.f(i6, i7, i8) || flag1;
                        } else {
                            flag0 = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            double d0 = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * (double) this.Z.nextFloat();
            double d1 = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * (double) this.Z.nextFloat();
            double d2 = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * (double) this.Z.nextFloat();

            this.o.a("largeexplode", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

        return flag0;
    }

    public boolean a(EntityDragonPart entitydragonpart, DamageSource damagesource, float f0) {
        if (entitydragonpart != this.bq) {
            f0 = f0 / 4.0F + 1.0F;
        }

        float f1 = this.y * 3.1415927F / 180.0F;
        float f2 = MathHelper.a(f1);
        float f3 = MathHelper.b(f1);

        this.h = this.s + (double) (f2 * 5.0F) + (double) ((this.Z.nextFloat() - 0.5F) * 2.0F);
        this.i = this.t + (double) (this.Z.nextFloat() * 3.0F) + 1.0D;
        this.bm = this.u - (double) (f3 * 5.0F) + (double) ((this.Z.nextFloat() - 0.5F) * 2.0F);
        this.bD = null;
        if (damagesource.j() instanceof EntityPlayer || damagesource.c()) {
            this.e(damagesource, f0);
        }

        return true;
    }

    public boolean a(DamageSource damagesource, float f0) {
        return false;
    }

    protected boolean e(DamageSource damagesource, float f0) {
        return super.a(damagesource, f0);
    }

    protected void aF() {
        ++this.bB;
        if (this.bB >= 180 && this.bB <= 200) {
            float f0 = (this.Z.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.Z.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.Z.nextFloat() - 0.5F) * 8.0F;

            this.o.a("hugeexplosion", this.s + (double) f0, this.t + 2.0D + (double) f1, this.u + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        int i0;
        int i1;

        if (!this.o.E) {
            if (this.bB > 150 && this.bB % 5 == 0) {
                i0 = 1000;

                while (i0 > 0) {
                    i1 = EntityXPOrb.a(i0);
                    i0 -= i1;
                    this.o.d((Entity) (new EntityXPOrb(this.o, this.s, this.t, this.u, i1)));
                }
            }

            if (this.bB == 1) {
                this.o.b(1018, (int) this.s, (int) this.t, (int) this.u, 0);
            }
        }

        this.d(0.0D, 0.10000000149011612D, 0.0D);
        this.aM = this.y += 20.0F;
        if (this.bB == 200 && !this.o.E) {
            i0 = 2000;

            while (i0 > 0) {
                i1 = EntityXPOrb.a(i0);
                i0 -= i1;
                this.o.d((Entity) (new EntityXPOrb(this.o, this.s, this.t, this.u, i1)));
            }

            this.b(MathHelper.c(this.s), MathHelper.c(this.u));
            this.B();
        }
    }

    private void b(int i0, int i1) {
        byte b0 = 64;

        BlockEndPortal.a = true;
        byte b1 = 4;

        for (int i2 = b0 - 1; i2 <= b0 + 32; ++i2) {
            for (int i3 = i0 - b1; i3 <= i0 + b1; ++i3) {
                for (int i4 = i1 - b1; i4 <= i1 + b1; ++i4) {
                    double d0 = (double) (i3 - i0);
                    double d1 = (double) (i4 - i1);
                    double d2 = d0 * d0 + d1 * d1;

                    if (d2 <= ((double) b1 - 0.5D) * ((double) b1 - 0.5D)) {
                        if (i2 < b0) {
                            if (d2 <= ((double) (b1 - 1) - 0.5D) * ((double) (b1 - 1) - 0.5D)) {
                                this.o.b(i3, i2, i4, Blocks.h);
                            }
                        } else if (i2 > b0) {
                            this.o.b(i3, i2, i4, Blocks.a);
                        } else if (d2 > ((double) (b1 - 1) - 0.5D) * ((double) (b1 - 1) - 0.5D)) {
                            this.o.b(i3, i2, i4, Blocks.h);
                        } else {
                            this.o.b(i3, i2, i4, Blocks.bq);
                        }
                    }
                }
            }
        }

        this.o.b(i0, b0 + 0, i1, Blocks.h);
        this.o.b(i0, b0 + 1, i1, Blocks.h);
        this.o.b(i0, b0 + 2, i1, Blocks.h);
        this.o.b(i0 - 1, b0 + 2, i1, Blocks.aa);
        this.o.b(i0 + 1, b0 + 2, i1, Blocks.aa);
        this.o.b(i0, b0 + 2, i1 - 1, Blocks.aa);
        this.o.b(i0, b0 + 2, i1 + 1, Blocks.aa);
        this.o.b(i0, b0 + 3, i1, Blocks.h);
        this.o.b(i0, b0 + 4, i1, Blocks.bt);
        BlockEndPortal.a = false;
    }

    protected void w() {
    }

    public Entity[] at() {
        return this.bp;
    }

    public boolean R() {
        return false;
    }

    public World a() {
        return this.o;
    }

    protected String t() {
        return "mob.enderdragon.growl";
    }

    protected String aT() {
        return "mob.enderdragon.hit";
    }

    protected float bf() {
        return 5.0F;
    }
}
