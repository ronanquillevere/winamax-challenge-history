package com.usesoft.poker.client.region;

public interface RegionVisitor<IN, OUT>
{
    OUT visitNorth(IN in);

    OUT visitSouth(IN in);

    OUT visitEast(IN in);

    OUT visitWest(IN in);

    OUT visitTop(IN in);

    OUT visitCenter(IN in);

    OUT visitBottom(IN in);

}
