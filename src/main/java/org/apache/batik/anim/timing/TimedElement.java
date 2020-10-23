// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import java.util.MissingResourceException;
import java.util.Locale;
import org.apache.batik.anim.AnimationException;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;
import java.util.HashSet;
import java.util.Calendar;
import org.apache.batik.parser.ClockParser;
import org.apache.batik.parser.ClockHandler;
import org.apache.batik.parser.ParseException;
import java.util.Set;
import org.w3c.dom.events.Event;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.batik.i18n.LocalizableSupport;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.util.SMILConstants;

public abstract class TimedElement implements SMILConstants
{
    public static final int FILL_REMOVE = 0;
    public static final int FILL_FREEZE = 1;
    public static final int RESTART_ALWAYS = 0;
    public static final int RESTART_WHEN_NOT_ACTIVE = 1;
    public static final int RESTART_NEVER = 2;
    public static final float INDEFINITE = Float.POSITIVE_INFINITY;
    public static final float UNRESOLVED = Float.NaN;
    protected TimedDocumentRoot root;
    protected TimeContainer parent;
    protected TimingSpecifier[] beginTimes;
    protected TimingSpecifier[] endTimes;
    protected float simpleDur;
    protected boolean durMedia;
    protected float repeatCount;
    protected float repeatDur;
    protected int currentRepeatIteration;
    protected float lastRepeatTime;
    protected int fillMode;
    protected int restartMode;
    protected float min;
    protected boolean minMedia;
    protected float max;
    protected boolean maxMedia;
    protected boolean isActive;
    protected boolean isFrozen;
    protected float lastSampleTime;
    protected float repeatDuration;
    protected List beginInstanceTimes;
    protected List endInstanceTimes;
    protected Interval currentInterval;
    protected float lastIntervalEnd;
    protected Interval previousInterval;
    protected LinkedList beginDependents;
    protected LinkedList endDependents;
    protected boolean shouldUpdateCurrentInterval;
    protected boolean hasParsed;
    protected Map handledEvents;
    protected boolean isSampling;
    protected boolean hasPropagated;
    protected static final String RESOURCES = "org.apache.batik.anim.resources.Messages";
    protected static LocalizableSupport localizableSupport;
    
    public TimedElement() {
        this.beginInstanceTimes = new ArrayList();
        this.endInstanceTimes = new ArrayList();
        this.beginDependents = new LinkedList();
        this.endDependents = new LinkedList();
        this.shouldUpdateCurrentInterval = true;
        this.handledEvents = new HashMap();
        this.beginTimes = new TimingSpecifier[0];
        this.endTimes = this.beginTimes;
        this.simpleDur = Float.NaN;
        this.repeatCount = Float.NaN;
        this.repeatDur = Float.NaN;
        this.lastRepeatTime = Float.NaN;
        this.max = Float.POSITIVE_INFINITY;
        this.lastSampleTime = Float.NaN;
        this.lastIntervalEnd = Float.NEGATIVE_INFINITY;
    }
    
    public TimedDocumentRoot getRoot() {
        return this.root;
    }
    
    public float getActiveTime() {
        return this.lastSampleTime;
    }
    
    public float getSimpleTime() {
        return this.lastSampleTime - this.lastRepeatTime;
    }
    
    protected float addInstanceTime(final InstanceTime time, final boolean isBegin) {
        this.hasPropagated = true;
        final List instanceTimes = isBegin ? this.beginInstanceTimes : this.endInstanceTimes;
        int index = Collections.binarySearch(instanceTimes, time);
        if (index < 0) {
            index = -(index + 1);
        }
        instanceTimes.add(index, time);
        this.shouldUpdateCurrentInterval = true;
        float ret;
        if (this.root.isSampling() && !this.isSampling) {
            ret = this.sampleAt(this.root.getCurrentTime(), this.root.isHyperlinking());
        }
        else {
            ret = Float.POSITIVE_INFINITY;
        }
        this.hasPropagated = false;
        this.root.currentIntervalWillUpdate();
        return ret;
    }
    
    protected float removeInstanceTime(final InstanceTime time, final boolean isBegin) {
        this.hasPropagated = true;
        final List instanceTimes = isBegin ? this.beginInstanceTimes : this.endInstanceTimes;
        int i;
        int index;
        for (index = (i = Collections.binarySearch(instanceTimes, time)); i >= 0; --i) {
            final InstanceTime it = instanceTimes.get(i);
            if (it == time) {
                instanceTimes.remove(i);
                break;
            }
            if (it.compareTo(time) != 0) {
                break;
            }
        }
        for (int len = instanceTimes.size(), j = index + 1; j < len; ++j) {
            final InstanceTime it2 = instanceTimes.get(j);
            if (it2 == time) {
                instanceTimes.remove(j);
                break;
            }
            if (it2.compareTo(time) != 0) {
                break;
            }
        }
        this.shouldUpdateCurrentInterval = true;
        float ret;
        if (this.root.isSampling() && !this.isSampling) {
            ret = this.sampleAt(this.root.getCurrentTime(), this.root.isHyperlinking());
        }
        else {
            ret = Float.POSITIVE_INFINITY;
        }
        this.hasPropagated = false;
        this.root.currentIntervalWillUpdate();
        return ret;
    }
    
    protected float instanceTimeChanged(final InstanceTime time, final boolean isBegin) {
        this.hasPropagated = true;
        this.shouldUpdateCurrentInterval = true;
        float ret;
        if (this.root.isSampling() && !this.isSampling) {
            ret = this.sampleAt(this.root.getCurrentTime(), this.root.isHyperlinking());
        }
        else {
            ret = Float.POSITIVE_INFINITY;
        }
        this.hasPropagated = false;
        return ret;
    }
    
    protected void addDependent(final TimingSpecifier dependent, final boolean forBegin) {
        if (forBegin) {
            this.beginDependents.add(dependent);
        }
        else {
            this.endDependents.add(dependent);
        }
    }
    
    protected void removeDependent(final TimingSpecifier dependent, final boolean forBegin) {
        if (forBegin) {
            this.beginDependents.remove(dependent);
        }
        else {
            this.endDependents.remove(dependent);
        }
    }
    
    public float getSimpleDur() {
        if (this.durMedia) {
            return this.getImplicitDur();
        }
        if (!isUnresolved(this.simpleDur)) {
            return this.simpleDur;
        }
        if (isUnresolved(this.repeatCount) && isUnresolved(this.repeatDur) && this.endTimes.length > 0) {
            return Float.POSITIVE_INFINITY;
        }
        return this.getImplicitDur();
    }
    
    public static boolean isUnresolved(final float t) {
        return Float.isNaN(t);
    }
    
    public float getActiveDur(final float B, final float end) {
        final float d = this.getSimpleDur();
        if (!isUnresolved(end) && d == Float.POSITIVE_INFINITY) {
            final float PAD = this.minusTime(end, B);
            return this.repeatDuration = this.minTime(this.max, this.maxTime(this.min, PAD));
        }
        float IAD;
        if (d == 0.0f) {
            IAD = 0.0f;
        }
        else if (isUnresolved(this.repeatDur) && isUnresolved(this.repeatCount)) {
            IAD = d;
        }
        else {
            final float p1 = isUnresolved(this.repeatCount) ? Float.POSITIVE_INFINITY : this.multiplyTime(d, this.repeatCount);
            final float p2 = isUnresolved(this.repeatDur) ? Float.POSITIVE_INFINITY : this.repeatDur;
            IAD = this.minTime(this.minTime(p1, p2), Float.POSITIVE_INFINITY);
        }
        float PAD;
        if (isUnresolved(end) || end == Float.POSITIVE_INFINITY) {
            PAD = IAD;
        }
        else {
            PAD = this.minTime(IAD, this.minusTime(end, B));
        }
        this.repeatDuration = IAD;
        return this.minTime(this.max, this.maxTime(this.min, PAD));
    }
    
    protected float minusTime(final float t1, final float t2) {
        if (isUnresolved(t1) || isUnresolved(t2)) {
            return Float.NaN;
        }
        if (t1 == Float.POSITIVE_INFINITY || t2 == Float.POSITIVE_INFINITY) {
            return Float.POSITIVE_INFINITY;
        }
        return t1 - t2;
    }
    
    protected float multiplyTime(final float t, final float n) {
        if (isUnresolved(t) || t == Float.POSITIVE_INFINITY) {
            return t;
        }
        return t * n;
    }
    
    protected float minTime(final float t1, final float t2) {
        if (t1 == 0.0f || t2 == 0.0f) {
            return 0.0f;
        }
        if ((t1 == Float.POSITIVE_INFINITY || isUnresolved(t1)) && t2 != Float.POSITIVE_INFINITY && !isUnresolved(t2)) {
            return t2;
        }
        if ((t2 == Float.POSITIVE_INFINITY || isUnresolved(t2)) && t1 != Float.POSITIVE_INFINITY && !isUnresolved(t1)) {
            return t1;
        }
        if ((t1 == Float.POSITIVE_INFINITY && isUnresolved(t2)) || (isUnresolved(t1) && t2 == Float.POSITIVE_INFINITY)) {
            return Float.POSITIVE_INFINITY;
        }
        if (t1 < t2) {
            return t1;
        }
        return t2;
    }
    
    protected float maxTime(final float t1, final float t2) {
        if ((t1 == Float.POSITIVE_INFINITY || isUnresolved(t1)) && t2 != Float.POSITIVE_INFINITY && !isUnresolved(t2)) {
            return t1;
        }
        if ((t2 == Float.POSITIVE_INFINITY || isUnresolved(t2)) && t1 != Float.POSITIVE_INFINITY && !isUnresolved(t1)) {
            return t2;
        }
        if ((t1 == Float.POSITIVE_INFINITY && isUnresolved(t2)) || (isUnresolved(t1) && t2 == Float.POSITIVE_INFINITY)) {
            return Float.NaN;
        }
        if (t1 > t2) {
            return t1;
        }
        return t2;
    }
    
    protected float getImplicitDur() {
        return Float.NaN;
    }
    
    protected float notifyNewInterval(final Interval interval) {
        float dependentMinTime = Float.POSITIVE_INFINITY;
        for (final TimingSpecifier ts : this.beginDependents) {
            final float t = ts.newInterval(interval);
            if (t < dependentMinTime) {
                dependentMinTime = t;
            }
        }
        for (final TimingSpecifier ts : this.endDependents) {
            final float t = ts.newInterval(interval);
            if (t < dependentMinTime) {
                dependentMinTime = t;
            }
        }
        return dependentMinTime;
    }
    
    protected float notifyRemoveInterval(final Interval interval) {
        float dependentMinTime = Float.POSITIVE_INFINITY;
        for (final TimingSpecifier ts : this.beginDependents) {
            final float t = ts.removeInterval(interval);
            if (t < dependentMinTime) {
                dependentMinTime = t;
            }
        }
        for (final TimingSpecifier ts : this.endDependents) {
            final float t = ts.removeInterval(interval);
            if (t < dependentMinTime) {
                dependentMinTime = t;
            }
        }
        return dependentMinTime;
    }
    
    protected float sampleAt(final float parentSimpleTime, boolean hyperlinking) {
        this.isSampling = true;
        final float time = parentSimpleTime;
        for (final Object o : this.handledEvents.entrySet()) {
            final Map.Entry e = (Map.Entry)o;
            final Event evt = e.getKey();
            final Set ts = e.getValue();
            Iterator j = ts.iterator();
            boolean hasBegin = false;
            boolean hasEnd = false;
            while (j.hasNext() && (!hasBegin || !hasEnd)) {
                final EventLikeTimingSpecifier t = j.next();
                if (t.isBegin()) {
                    hasBegin = true;
                }
                else {
                    hasEnd = true;
                }
            }
            boolean useBegin;
            boolean useEnd;
            if (hasBegin && hasEnd) {
                useBegin = (!this.isActive || this.restartMode == 0);
                useEnd = !useBegin;
            }
            else if (hasBegin && (!this.isActive || this.restartMode == 0)) {
                useBegin = true;
                useEnd = false;
            }
            else {
                if (!hasEnd || !this.isActive) {
                    continue;
                }
                useBegin = false;
                useEnd = true;
            }
            j = ts.iterator();
            while (j.hasNext()) {
                final EventLikeTimingSpecifier t2 = j.next();
                final boolean isBegin = t2.isBegin();
                if ((isBegin && useBegin) || (!isBegin && useEnd)) {
                    t2.resolve(evt);
                    this.shouldUpdateCurrentInterval = true;
                }
            }
        }
        this.handledEvents.clear();
        if (this.currentInterval != null) {
            final float begin = this.currentInterval.getBegin();
            if (this.lastSampleTime < begin && time >= begin) {
                if (!this.isActive) {
                    this.toActive(begin);
                }
                this.isActive = true;
                this.isFrozen = false;
                this.lastRepeatTime = begin;
                this.fireTimeEvent("beginEvent", this.currentInterval.getBegin(), 0);
            }
        }
        boolean hasEnded = this.currentInterval != null && time >= this.currentInterval.getEnd();
        if (this.currentInterval != null) {
            final float begin2 = this.currentInterval.getBegin();
            if (time >= begin2) {
                final float d = this.getSimpleDur();
                while (time - this.lastRepeatTime >= d && this.lastRepeatTime + d < begin2 + this.repeatDuration) {
                    this.lastRepeatTime += d;
                    ++this.currentRepeatIteration;
                    this.fireTimeEvent(this.root.getRepeatEventName(), this.lastRepeatTime, this.currentRepeatIteration);
                }
            }
        }
        float dependentMinTime = Float.POSITIVE_INFINITY;
        if (hyperlinking) {
            this.shouldUpdateCurrentInterval = true;
        }
        while (this.shouldUpdateCurrentInterval || hasEnded) {
            if (hasEnded) {
                this.previousInterval = this.currentInterval;
                this.toInactive(this.isActive = false, this.isFrozen = (this.fillMode == 1));
                this.fireTimeEvent("endEvent", this.currentInterval.getEnd(), 0);
            }
            final boolean first = this.currentInterval == null && this.previousInterval == null;
            if (this.currentInterval != null && hyperlinking) {
                this.isActive = false;
                this.toInactive(this.isFrozen = false, false);
                this.currentInterval = null;
            }
            if (this.currentInterval == null || hasEnded) {
                if (first || hyperlinking || this.restartMode != 2) {
                    boolean incl = true;
                    float beginAfter;
                    if (first || hyperlinking) {
                        beginAfter = Float.NEGATIVE_INFINITY;
                    }
                    else {
                        beginAfter = this.previousInterval.getEnd();
                        incl = (beginAfter != this.previousInterval.getBegin());
                    }
                    final Interval interval = this.computeInterval(first, false, beginAfter, incl);
                    if (interval == null) {
                        this.currentInterval = null;
                    }
                    else {
                        final float dmt = this.selectNewInterval(time, interval);
                        if (dmt < dependentMinTime) {
                            dependentMinTime = dmt;
                        }
                    }
                }
                else {
                    this.currentInterval = null;
                }
            }
            else {
                final float currentBegin = this.currentInterval.getBegin();
                if (currentBegin > time) {
                    boolean incl2 = true;
                    float beginAfter2;
                    if (this.previousInterval == null) {
                        beginAfter2 = Float.NEGATIVE_INFINITY;
                    }
                    else {
                        beginAfter2 = this.previousInterval.getEnd();
                        incl2 = (beginAfter2 != this.previousInterval.getBegin());
                    }
                    final Interval interval2 = this.computeInterval(false, false, beginAfter2, incl2);
                    float dmt2 = this.notifyRemoveInterval(this.currentInterval);
                    if (dmt2 < dependentMinTime) {
                        dependentMinTime = dmt2;
                    }
                    if (interval2 == null) {
                        this.currentInterval = null;
                    }
                    else {
                        dmt2 = this.selectNewInterval(time, interval2);
                        if (dmt2 < dependentMinTime) {
                            dependentMinTime = dmt2;
                        }
                    }
                }
                else {
                    final Interval interval3 = this.computeInterval(false, true, currentBegin, true);
                    final float newEnd = interval3.getEnd();
                    if (this.currentInterval.getEnd() != newEnd) {
                        final float dmt = this.currentInterval.setEnd(newEnd, interval3.getEndInstanceTime());
                        if (dmt < dependentMinTime) {
                            dependentMinTime = dmt;
                        }
                    }
                }
            }
            this.shouldUpdateCurrentInterval = false;
            hyperlinking = false;
            hasEnded = (this.currentInterval != null && time >= this.currentInterval.getEnd());
        }
        final float d = this.getSimpleDur();
        if (this.isActive && !this.isFrozen) {
            if (time - this.currentInterval.getBegin() >= this.repeatDuration) {
                this.toInactive(true, this.isFrozen = (this.fillMode == 1));
            }
            else {
                this.sampledAt(time - this.lastRepeatTime, d, this.currentRepeatIteration);
            }
        }
        if (this.isFrozen) {
            float t3;
            boolean atLast;
            if (this.isActive) {
                t3 = this.currentInterval.getBegin() + this.repeatDuration - this.lastRepeatTime;
                atLast = (this.lastRepeatTime + d == this.currentInterval.getBegin() + this.repeatDuration);
            }
            else {
                t3 = this.previousInterval.getEnd() - this.lastRepeatTime;
                atLast = (this.lastRepeatTime + d == this.previousInterval.getEnd());
            }
            if (atLast) {
                this.sampledLastValue(this.currentRepeatIteration);
            }
            else {
                this.sampledAt(t3 % d, d, this.currentRepeatIteration);
            }
        }
        else if (!this.isActive) {}
        this.isSampling = false;
        this.lastSampleTime = time;
        if (this.currentInterval == null) {
            return dependentMinTime;
        }
        float t3 = this.currentInterval.getBegin() - time;
        if (t3 <= 0.0f) {
            t3 = ((this.isConstantAnimation() || this.isFrozen) ? (this.currentInterval.getEnd() - time) : 0.0f);
        }
        if (dependentMinTime < t3) {
            return dependentMinTime;
        }
        return t3;
    }
    
    protected boolean endHasEventConditions() {
        for (final TimingSpecifier endTime : this.endTimes) {
            if (endTime.isEventCondition()) {
                return true;
            }
        }
        return false;
    }
    
    protected float selectNewInterval(final float time, final Interval interval) {
        this.currentInterval = interval;
        final float dmt = this.notifyNewInterval(this.currentInterval);
        float beginEventTime = this.currentInterval.getBegin();
        if (time >= beginEventTime) {
            this.lastRepeatTime = beginEventTime;
            if (beginEventTime < 0.0f) {
                beginEventTime = 0.0f;
            }
            this.toActive(beginEventTime);
            this.isActive = true;
            this.isFrozen = false;
            this.fireTimeEvent("beginEvent", beginEventTime, 0);
            final float d = this.getSimpleDur();
            final float end = this.currentInterval.getEnd();
            while (time - this.lastRepeatTime >= d && this.lastRepeatTime + d < end) {
                this.lastRepeatTime += d;
                ++this.currentRepeatIteration;
                this.fireTimeEvent(this.root.getRepeatEventName(), this.lastRepeatTime, this.currentRepeatIteration);
            }
        }
        return dmt;
    }
    
    protected Interval computeInterval(final boolean first, final boolean fixedBegin, float beginAfter, final boolean incl) {
        final Iterator beginIterator = this.beginInstanceTimes.iterator();
        final Iterator endIterator = this.endInstanceTimes.iterator();
        final float parentSimpleDur = this.parent.getSimpleDur();
        InstanceTime endInstanceTime = endIterator.hasNext() ? endIterator.next() : null;
        boolean firstEnd = true;
        InstanceTime beginInstanceTime = null;
        InstanceTime nextBeginInstanceTime = null;
        while (true) {
            float tempBegin = 0.0f;
            Label_0206: {
                if (!fixedBegin) {
                    while (beginIterator.hasNext()) {
                        beginInstanceTime = beginIterator.next();
                        tempBegin = beginInstanceTime.getTime();
                        if ((incl && tempBegin >= beginAfter) || (!incl && tempBegin > beginAfter)) {
                            if (!beginIterator.hasNext()) {
                                break Label_0206;
                            }
                            nextBeginInstanceTime = beginIterator.next();
                            if (beginInstanceTime.getTime() != nextBeginInstanceTime.getTime()) {
                                break Label_0206;
                            }
                            nextBeginInstanceTime = null;
                        }
                    }
                    return null;
                }
                tempBegin = beginAfter;
                while (beginIterator.hasNext()) {
                    nextBeginInstanceTime = beginIterator.next();
                    if (nextBeginInstanceTime.getTime() > tempBegin) {
                        break;
                    }
                }
            }
            if (tempBegin >= parentSimpleDur) {
                return null;
            }
            float tempEnd;
            if (this.endTimes.length == 0) {
                tempEnd = tempBegin + this.getActiveDur(tempBegin, Float.POSITIVE_INFINITY);
            }
            else {
                if (this.endInstanceTimes.isEmpty()) {
                    tempEnd = Float.NaN;
                }
                else {
                    tempEnd = endInstanceTime.getTime();
                    Label_0387: {
                        if ((first && !firstEnd && tempEnd == tempBegin) || (!first && this.currentInterval != null && tempEnd == this.currentInterval.getEnd() && ((incl && beginAfter >= tempEnd) || (!incl && beginAfter > tempEnd)))) {
                            while (endIterator.hasNext()) {
                                endInstanceTime = endIterator.next();
                                tempEnd = endInstanceTime.getTime();
                                if (tempEnd > tempBegin) {
                                    break Label_0387;
                                }
                            }
                            if (!this.endHasEventConditions()) {
                                return null;
                            }
                            tempEnd = Float.NaN;
                        }
                    }
                    firstEnd = false;
                    while (tempEnd < tempBegin) {
                        if (!endIterator.hasNext()) {
                            if (this.endHasEventConditions()) {
                                tempEnd = Float.NaN;
                                break;
                            }
                            return null;
                        }
                        else {
                            endInstanceTime = endIterator.next();
                            tempEnd = endInstanceTime.getTime();
                        }
                    }
                }
                final float ad = this.getActiveDur(tempBegin, tempEnd);
                tempEnd = tempBegin + ad;
            }
            if (!first || tempEnd > 0.0f || (tempBegin == 0.0f && tempEnd == 0.0f) || isUnresolved(tempEnd)) {
                if (this.restartMode == 0 && nextBeginInstanceTime != null) {
                    final float nextBegin = nextBeginInstanceTime.getTime();
                    if (nextBegin < tempEnd || isUnresolved(tempEnd)) {
                        tempEnd = nextBegin;
                        endInstanceTime = nextBeginInstanceTime;
                    }
                }
                final Interval i = new Interval(tempBegin, tempEnd, beginInstanceTime, endInstanceTime);
                return i;
            }
            if (fixedBegin) {
                return null;
            }
            beginAfter = tempEnd;
        }
    }
    
    protected void reset(final boolean clearCurrentBegin) {
        Iterator i = this.beginInstanceTimes.iterator();
        while (i.hasNext()) {
            final InstanceTime it = i.next();
            if (it.getClearOnReset() && (clearCurrentBegin || this.currentInterval == null || this.currentInterval.getBeginInstanceTime() != it)) {
                i.remove();
            }
        }
        i = this.endInstanceTimes.iterator();
        while (i.hasNext()) {
            final InstanceTime it = i.next();
            if (it.getClearOnReset()) {
                i.remove();
            }
        }
        if (this.isFrozen) {
            this.removeFill();
        }
        this.currentRepeatIteration = 0;
        this.lastRepeatTime = Float.NaN;
        this.isActive = false;
        this.isFrozen = false;
        this.lastSampleTime = Float.NaN;
    }
    
    public void parseAttributes(final String begin, final String dur, final String end, final String min, final String max, final String repeatCount, final String repeatDur, final String fill, final String restart) {
        if (!this.hasParsed) {
            this.parseBegin(begin);
            this.parseDur(dur);
            this.parseEnd(end);
            this.parseMin(min);
            this.parseMax(max);
            if (this.min > this.max) {
                this.min = 0.0f;
                this.max = Float.POSITIVE_INFINITY;
            }
            this.parseRepeatCount(repeatCount);
            this.parseRepeatDur(repeatDur);
            this.parseFill(fill);
            this.parseRestart(restart);
            this.hasParsed = true;
        }
    }
    
    protected void parseBegin(String begin) {
        try {
            if (begin.length() == 0) {
                begin = "0";
            }
            this.beginTimes = TimingSpecifierListProducer.parseTimingSpecifierList(this, true, begin, this.root.useSVG11AccessKeys, this.root.useSVG12AccessKeys);
        }
        catch (ParseException ex) {
            throw this.createException("attribute.malformed", new Object[] { null, "begin" });
        }
    }
    
    protected void parseDur(final String dur) {
        if (dur.equals("media")) {
            this.durMedia = true;
            this.simpleDur = Float.NaN;
        }
        else {
            this.durMedia = false;
            if (dur.length() == 0 || dur.equals("indefinite")) {
                this.simpleDur = Float.POSITIVE_INFINITY;
            }
            else {
                try {
                    this.simpleDur = this.parseClockValue(dur, false);
                }
                catch (ParseException e) {
                    throw this.createException("attribute.malformed", new Object[] { null, "dur" });
                }
                if (this.simpleDur < 0.0f) {
                    this.simpleDur = Float.POSITIVE_INFINITY;
                }
            }
        }
    }
    
    protected float parseClockValue(final String s, final boolean parseOffset) throws ParseException {
        final ClockParser p = new ClockParser(parseOffset);
        class Handler implements ClockHandler
        {
            protected float v;
            
            Handler() {
                this.v = 0.0f;
            }
            
            @Override
            public void clockValue(final float newClockValue) {
                this.v = newClockValue;
            }
        }
        final Handler h = new Handler();
        p.setClockHandler(h);
        p.parse(s);
        return h.v;
    }
    
    protected void parseEnd(final String end) {
        try {
            this.endTimes = TimingSpecifierListProducer.parseTimingSpecifierList(this, false, end, this.root.useSVG11AccessKeys, this.root.useSVG12AccessKeys);
        }
        catch (ParseException ex) {
            throw this.createException("attribute.malformed", new Object[] { null, "end" });
        }
    }
    
    protected void parseMin(final String min) {
        if (min.equals("media")) {
            this.min = 0.0f;
            this.minMedia = true;
        }
        else {
            this.minMedia = false;
            if (min.length() == 0) {
                this.min = 0.0f;
            }
            else {
                try {
                    this.min = this.parseClockValue(min, false);
                }
                catch (ParseException ex) {
                    this.min = 0.0f;
                }
                if (this.min < 0.0f) {
                    this.min = 0.0f;
                }
            }
        }
    }
    
    protected void parseMax(final String max) {
        if (max.equals("media")) {
            this.max = Float.POSITIVE_INFINITY;
            this.maxMedia = true;
        }
        else {
            this.maxMedia = false;
            if (max.length() == 0 || max.equals("indefinite")) {
                this.max = Float.POSITIVE_INFINITY;
            }
            else {
                try {
                    this.max = this.parseClockValue(max, false);
                }
                catch (ParseException ex) {
                    this.max = Float.POSITIVE_INFINITY;
                }
                if (this.max < 0.0f) {
                    this.max = 0.0f;
                }
            }
        }
    }
    
    protected void parseRepeatCount(final String repeatCount) {
        if (repeatCount.length() == 0) {
            this.repeatCount = Float.NaN;
        }
        else if (repeatCount.equals("indefinite")) {
            this.repeatCount = Float.POSITIVE_INFINITY;
        }
        else {
            try {
                this.repeatCount = Float.parseFloat(repeatCount);
                if (this.repeatCount > 0.0f) {
                    return;
                }
            }
            catch (NumberFormatException ex) {
                throw this.createException("attribute.malformed", new Object[] { null, "repeatCount" });
            }
        }
    }
    
    protected void parseRepeatDur(final String repeatDur) {
        try {
            if (repeatDur.length() == 0) {
                this.repeatDur = Float.NaN;
            }
            else if (repeatDur.equals("indefinite")) {
                this.repeatDur = Float.POSITIVE_INFINITY;
            }
            else {
                this.repeatDur = this.parseClockValue(repeatDur, false);
            }
        }
        catch (ParseException ex) {
            throw this.createException("attribute.malformed", new Object[] { null, "repeatDur" });
        }
    }
    
    protected void parseFill(final String fill) {
        if (fill.length() == 0 || fill.equals("remove")) {
            this.fillMode = 0;
        }
        else {
            if (!fill.equals("freeze")) {
                throw this.createException("attribute.malformed", new Object[] { null, "fill" });
            }
            this.fillMode = 1;
        }
    }
    
    protected void parseRestart(final String restart) {
        if (restart.length() == 0 || restart.equals("always")) {
            this.restartMode = 0;
        }
        else if (restart.equals("whenNotActive")) {
            this.restartMode = 1;
        }
        else {
            if (!restart.equals("never")) {
                throw this.createException("attribute.malformed", new Object[] { null, "restart" });
            }
            this.restartMode = 2;
        }
    }
    
    public void initialize() {
        for (final TimingSpecifier beginTime : this.beginTimes) {
            beginTime.initialize();
        }
        for (final TimingSpecifier endTime : this.endTimes) {
            endTime.initialize();
        }
    }
    
    public void deinitialize() {
        for (final TimingSpecifier beginTime : this.beginTimes) {
            beginTime.deinitialize();
        }
        for (final TimingSpecifier endTime : this.endTimes) {
            endTime.deinitialize();
        }
    }
    
    public void beginElement() {
        this.beginElement(0.0f);
    }
    
    public void beginElement(final float offset) {
        final float t = this.root.convertWallclockTime(Calendar.getInstance());
        final InstanceTime it = new InstanceTime(null, t + offset, true);
        this.addInstanceTime(it, true);
    }
    
    public void endElement() {
        this.endElement(0.0f);
    }
    
    public void endElement(final float offset) {
        final float t = this.root.convertWallclockTime(Calendar.getInstance());
        final InstanceTime it = new InstanceTime(null, t + offset, true);
        this.addInstanceTime(it, false);
    }
    
    public float getLastSampleTime() {
        return this.lastSampleTime;
    }
    
    public float getCurrentBeginTime() {
        final float begin;
        if (this.currentInterval == null || (begin = this.currentInterval.getBegin()) < this.lastSampleTime) {
            return Float.NaN;
        }
        return begin;
    }
    
    public boolean canBegin() {
        return this.currentInterval == null || (this.isActive && this.restartMode != 2);
    }
    
    public boolean canEnd() {
        return this.isActive;
    }
    
    public float getHyperlinkBeginTime() {
        if (this.isActive) {
            return this.currentInterval.getBegin();
        }
        if (!this.beginInstanceTimes.isEmpty()) {
            return this.beginInstanceTimes.get(0).getTime();
        }
        return Float.NaN;
    }
    
    public TimingSpecifier[] getBeginTimingSpecifiers() {
        return this.beginTimes.clone();
    }
    
    public TimingSpecifier[] getEndTimingSpecifiers() {
        return this.endTimes.clone();
    }
    
    protected void fireTimeEvent(final String eventType, final float time, final int detail) {
        final Calendar t = (Calendar)this.root.getDocumentBeginTime().clone();
        t.add(14, (int)Math.round(time * 1000.0));
        this.fireTimeEvent(eventType, t, detail);
    }
    
    void eventOccurred(final TimingSpecifier t, final Event e) {
        Set ts = this.handledEvents.get(e);
        if (ts == null) {
            ts = new HashSet();
            this.handledEvents.put(e, ts);
        }
        ts.add(t);
        this.root.currentIntervalWillUpdate();
    }
    
    protected abstract void fireTimeEvent(final String p0, final Calendar p1, final int p2);
    
    protected abstract void toActive(final float p0);
    
    protected abstract void toInactive(final boolean p0, final boolean p1);
    
    protected abstract void removeFill();
    
    protected abstract void sampledAt(final float p0, final float p1, final int p2);
    
    protected abstract void sampledLastValue(final int p0);
    
    protected abstract TimedElement getTimedElementById(final String p0);
    
    protected abstract EventTarget getEventTargetById(final String p0);
    
    protected abstract EventTarget getRootEventTarget();
    
    public abstract Element getElement();
    
    protected abstract EventTarget getAnimationEventTarget();
    
    public abstract boolean isBefore(final TimedElement p0);
    
    protected abstract boolean isConstantAnimation();
    
    public AnimationException createException(final String code, final Object[] params) {
        final Element e = this.getElement();
        if (e != null) {
            params[0] = e.getNodeName();
        }
        return new AnimationException(this, code, params);
    }
    
    public static void setLocale(final Locale l) {
        TimedElement.localizableSupport.setLocale(l);
    }
    
    public static Locale getLocale() {
        return TimedElement.localizableSupport.getLocale();
    }
    
    public static String formatMessage(final String key, final Object[] args) throws MissingResourceException {
        return TimedElement.localizableSupport.formatMessage(key, args);
    }
    
    public static String toString(final float time) {
        if (Float.isNaN(time)) {
            return "UNRESOLVED";
        }
        if (time == Float.POSITIVE_INFINITY) {
            return "INDEFINITE";
        }
        return Float.toString(time);
    }
    
    static {
        TimedElement.localizableSupport = new LocalizableSupport("org.apache.batik.anim.resources.Messages", TimedElement.class.getClassLoader());
    }
}
