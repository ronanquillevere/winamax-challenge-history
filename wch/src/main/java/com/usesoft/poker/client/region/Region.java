package com.usesoft.poker.client.region;

public enum Region
{
    North
    {
        @Override
        public <IN, OUT> OUT accept(RegionVisitor<IN, OUT> visitor, IN in)
        {
            return visitor.visitNorth(in);
        }
    },
    South
    {
        @Override
        public <IN, OUT> OUT accept(RegionVisitor<IN, OUT> visitor, IN in)
        {
            return visitor.visitSouth(in);
        }
    },
    East
    {
        @Override
        public <IN, OUT> OUT accept(RegionVisitor<IN, OUT> visitor, IN in)
        {
            return visitor.visitEast(in);
        }
    },
    West
    {
        @Override
        public <IN, OUT> OUT accept(RegionVisitor<IN, OUT> visitor, IN in)
        {
            return visitor.visitWest(in);
        }
    },
    Top
    {
        @Override
        public <IN, OUT> OUT accept(RegionVisitor<IN, OUT> visitor, IN in)
        {
            return visitor.visitTop(in);
        }
    },
    Center
    {
        @Override
        public <IN, OUT> OUT accept(RegionVisitor<IN, OUT> visitor, IN in)
        {
            return visitor.visitCenter(in);
        }
    },
    Bottom
    {
        @Override
        public <IN, OUT> OUT accept(RegionVisitor<IN, OUT> visitor, IN in)
        {
            return visitor.visitBottom(in);
        }
    };

    public abstract <IN, OUT> OUT accept(RegionVisitor<IN, OUT> visitor, IN in);
}
