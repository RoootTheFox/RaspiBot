package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

// most of this code is basically just stolen from KaptainWutax. (github.com/KaptainWutax)
// It is licensed under the MIT license.
/* The MIT License (MIT)

Copyright (c) 2020 Unknown

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
@SuppressWarnings("unused")
public class ShadowSeedCommand implements Command {
    public static final long A = 6364136223846793005L;
    public static final long B = 1442695040888963407L;

    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if(args.length == 0) {
            msg.getChannel().sendMessage("No seed given!").complete();
            System.exit(0);
        }
        long seed = Long.parseLong(args[0]);

        long nextSeed = mixSeed(seed, 0L);
        long shadowSeed = unmixSeed(nextSeed, 0L, Solution.of(~seed));
        msg.getChannel().sendMessage(String.valueOf(shadowSeed)).complete();
    }

    @Override
    public String getName() { return "shadowseed"; }

    @Override
    public String getDescription() { return "Find the shadow of a Minecraft seed."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }


    public enum Solution {
        EVEN, ODD;

        public static Solution of(long n) {
            return values()[(int)(n & 1)];
        }
    }

    public static long mixSeed(long seed, long salt) {
        seed *= seed * A + B;
        seed += salt;
        return seed;
    }

    public static long unmixSeed(long seed, long salt, Solution solution) {
        long r = solution.ordinal();

        for(int j = 1; j < 64; j <<= 1) {
            r = r - (A * r * r + B * r + salt - seed) * modInverse(2 * A * r + B, 64);
        }

        return r;
    }

    public static long modInverse(long a, int k) {
        long x = ((((a << 1) ^ a) & 4) << 1) ^ a;

        x += x - a * x * x;
        x += x - a * x * x;
        x += x - a * x * x;
        x += x - a * x * x;

        return x & mask(k);
    }

    public static long pow2(int bits) {
        return 1L << bits;
    }

    public static long mask(int bits) {
        if(bits >= 64) {
            return ~0;
        }

        return pow2(bits) - 1;
    }
}
