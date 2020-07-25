package net.eterniamc.scheduler.sync;

@SuppressWarnings("unused")
public enum Rate {

    MIN_64(3840000L),
    MIN_32(1920000L),
    MIN_16(960000L),
    MIN_08(480000L),
    MIN_04(240000L),
    MIN_02(120000L),
    MIN_01(60000L),
    SLOWEST(32000L),
    SLOWER(16000L),
    SEC_10(10000L),
    SEC_8(8000L),
    SEC_6(6000L),
    SEC_4(4000L),
    SEC_2(2000L),
    SEC(1000L),
    FAST(500L),
    FASTER(250L),
    FASTEST(125L),
    TICK(50L),
    HALF_TICK(25),
    QUARTER_TICK(12),
    INSTANT(1L);

    private final long time;

    Rate(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
